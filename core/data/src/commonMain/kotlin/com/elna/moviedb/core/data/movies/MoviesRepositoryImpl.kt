package com.elna.moviedb.core.data.movies


import com.elna.moviedb.core.data.model.asEntity
import com.elna.moviedb.core.data.util.toFullImageUrl
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.asEntity
import com.elna.moviedb.core.datastore.PaginationPreferences
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.datastore.model.PaginationState
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.core.network.MoviesRemoteDataSource
import com.elna.moviedb.core.network.model.movies.toDomain
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementation of MoviesRepository that manages movie data from remote API and local cache.
 *
 * This repository follows the Open/Closed Principle by using category abstraction.
 * New movie categories can be added to [MovieCategory] enum without modifying this class.
 *
 * This repository provides data access operations only. Language change coordination
 * is handled separately by [com.elna.moviedb.core.data.LanguageChangeCoordinator].
 *
 * @param moviesRemoteDataSource Remote data source for fetching movies from API
 * @param moviesLocalDataSource Local data source for caching movies in database
 * @param paginationPreferences Manager for pagination state
 * @param preferencesManager Manager for app settings (language)
 */
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val paginationPreferences: PaginationPreferences,
    private val preferencesManager: PreferencesManager,
) : MoviesRepository {

    /**
     * Observes movies for a specific category from local storage.
     *
     * Returns a flow of movies from the local cache. Automatically triggers
     * initial load if cache is empty for the given category.
     *
     * This method follows the Open/Closed Principle - new categories can be
     * added to MovieCategory enum without modifying this method.
     */
    override suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>> {
        val localMoviesPageStream =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(category.name)

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadMoviesNextPage(category)
        }

        return localMoviesPageStream.map { movieEntities ->
            movieEntities.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath.toFullImageUrl()
                )
            }
        }
    }

    /**
     * Loads the next page of movies for a specific category from the remote API.
     *
     * This method follows the Open/Closed Principle - new categories can be
     * added to MovieCategory enum without modifying this method.
     *
     * @param category The movie category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
        val currentLanguage = getLanguage()
        val paginationState = paginationPreferences.getPaginationState(category.name).first()

        if (paginationState.totalPages > 0 && paginationState.currentPage >= paginationState.totalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = paginationState.currentPage + 1

        return when (val result =
            moviesRemoteDataSource.fetchMoviesPage(category.apiPath, nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map {
                    it.asEntity().copy(category = category.name)
                }
                moviesLocalDataSource.insertMoviesPage(entities)

                // Save pagination state
                paginationPreferences.savePaginationState(
                    category.name,
                    PaginationState(
                        currentPage = nextPage,
                        totalPages = newTotalPages
                    )
                )

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Retrieves detailed information for a specific movie using offline-first strategy.
     *
     * This function implements an offline-first approach:
     * 1. First checks local storage for cached movie details and trailers
     * 2. If found, returns cached data immediately (fast response)
     * 3. If not found locally, fetches from remote API in parallel
     * 4. Caches the remote result (both details and trailers) locally for future use
     * 5. Returns the movie details with trailers converted to domain model
     *
     * @param movieId The unique identifier of the movie to retrieve
     * @return AppResult<MovieDetails> Success with movie details and trailers or Error if fetch failed and no cache available
     */
    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> = coroutineScope {
        // 1. Check cache first (offline-first)
        val cachedMovieDetails = moviesLocalDataSource.getMoviesDetails(movieId)
        val cachedVideos = moviesLocalDataSource.getVideosForMovie(movieId)
        val cachedCast = moviesLocalDataSource.getCastForMovie(movieId)

        // 2. If all cached, return immediately
        if (cachedMovieDetails != null) {
            val trailers = cachedVideos.map { it.toDomain() }
            val cast = cachedCast.sortedBy { it.order }
                .map { it.toDomain().copy(profilePath = it.profilePath.toFullImageUrl()) }

            val movieDetails = cachedMovieDetails.toDomain().copy(
                posterPath = cachedMovieDetails.posterPath.toFullImageUrl(),
                backdropPath = cachedMovieDetails.backdropPath.toFullImageUrl(),
                trailers = trailers,
                cast = cast
            )
            return@coroutineScope AppResult.Success(movieDetails)
        }

        // 3. Cache miss - fetch from network in parallel
        val language = getLanguage()
        val detailsDeferred = async { moviesRemoteDataSource.getMovieDetails(movieId, language) }
        val videosDeferred = async { moviesRemoteDataSource.getMovieVideos(movieId, language) }
        val creditsDeferred = async { moviesRemoteDataSource.getMovieCredits(movieId, language) }

        val detailsResult = detailsDeferred.await()
        // 4. Extract details or return error (cancels videosDeferred and creditsDeferred on return)
        val details = when (detailsResult) {
            is AppResult.Success -> detailsResult.data
            is AppResult.Error -> return@coroutineScope detailsResult
        }
        // Only await videos and credits after details succeeded
        val videosResult = videosDeferred.await()
        val creditsResult = creditsDeferred.await()

        // 5. Process videos (optional - don't fail if videos error)
        val trailers = when (videosResult) {
            is AppResult.Success -> {
                videosResult.data.results
                    .filter { it.type == "Trailer" || it.type == "Teaser" }
                    .sortedWith(compareByDescending<RemoteVideo> { it.official }
                        .thenByDescending { it.publishedAt })
                    .map { it.toDomain() }
            }

            is AppResult.Error -> emptyList()  // Graceful degradation
        }

        // 6. Process cast (optional - don't fail if cast errors)
        val remoteCast = when (creditsResult) {
            is AppResult.Success -> {
                creditsResult.data.cast
                    ?.sortedBy { it.order }
                    ?: emptyList()
            }

            is AppResult.Error -> emptyList()  // Graceful degradation
        }

        // 7. Cache everything for future offline access
        val detailsEntity = details.asEntity()
        moviesLocalDataSource.insertMovieDetails(detailsEntity)

        // Replace existing trailers atomically
        moviesLocalDataSource.deleteVideosForMovie(movieId)
        if (trailers.isNotEmpty()) {
            val videoEntities = trailers.map {
                it.asEntity(movieId = movieId)
            }
            moviesLocalDataSource.insertVideos(videoEntities)
        }

        val castEntities = remoteCast.map { remoteCastMember ->
            CastMemberEntity(
                movieId = movieId,
                personId = remoteCastMember.id,
                name = remoteCastMember.name,
                character = remoteCastMember.character,
                profilePath = remoteCastMember.profilePath,
                order = remoteCastMember.order
            )
        }
        moviesLocalDataSource.replaceCastForMovie(movieId, castEntities)

        // Convert cast to domain for return
        val cast = remoteCast.map { remoteCastMember ->
            remoteCastMember.toDomain().copy(
                profilePath = remoteCastMember.profilePath.toFullImageUrl()
            )
        }

        // 8. Return combined result with full URLs
        val movieDetails = detailsEntity.toDomain().copy(
            posterPath = detailsEntity.posterPath.toFullImageUrl(),
            backdropPath = detailsEntity.backdropPath.toFullImageUrl(),
            trailers = trailers,
            cast = cast
        )
        AppResult.Success(movieDetails)
    }

    /**
     * Clears all cached movies and reloads initial pages for all categories.
     *
     * This method is called by [com.elna.moviedb.core.data.LanguageChangeCoordinator] when the app language changes.
     * It clears the local cache and fetches fresh data in the new language.
     *
     * This method automatically handles all categories defined in [MovieCategory] enum.
     */
    override suspend fun clearAndReload() {
        // Clear all pagination state and local data
        paginationPreferences.clearAllPaginationState()
        moviesLocalDataSource.clearAllMovies()

        // Load all categories in parallel
        coroutineScope {
            MovieCategory.entries.forEach { category ->
                async { loadMoviesNextPage(category) }
            }
        }
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
