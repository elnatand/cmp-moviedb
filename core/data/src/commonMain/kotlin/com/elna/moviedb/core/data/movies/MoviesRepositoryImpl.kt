package com.elna.moviedb.core.data.movies


import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.data.model.asEntity
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.database.model.MovieCategory
import com.elna.moviedb.core.database.model.asEntity
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.datastore.model.PaginationState
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.network.MoviesRemoteDataSource
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import com.elna.moviedb.core.network.model.movies.toDomain
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Implementation of MoviesRepository that manages movie data from remote API and local cache.
 *
 * **Lifecycle:** This repository is an application-scoped singleton managed by Koin DI.
 * The [repositoryScope] is never cancelled and lives for the entire application lifetime.
 * This is intentional as the repository maintains app-wide state and language change observers.
 *
 * @param moviesRemoteDataSource Remote data source for fetching movies from API
 * @param moviesLocalDataSource Local data source for caching movies in database
 * @param preferencesManager Manager for accessing app preferences (language, etc.)
 * @param appDispatchers Dispatcher provider for coroutine execution
 */
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers
) : MoviesRepository {

    /**
     * Application-scoped coroutine scope that lives for the entire app lifetime.
     * Never cancelled as this repository is a singleton.
     */
    private val repositoryScope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    init {
        // Listen to language changes and clear movies when language changes
        repositoryScope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on screen entry
                .collect {
                    clearMovies()
                    loadPopularMoviesNextPage()
                    loadTopRatedMoviesNextPage()
                    loadNowPlayingMoviesNextPage()
                }
        }
    }

    /**
     * Observes popular movies from local storage.
     * Returns a flow of movies from the local cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observePopularMovies(): Flow<List<Movie>> {
        val localMoviesPageStream =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(MovieCategory.POPULAR.name)

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadPopularMoviesNextPage()
        }

        return localMoviesPageStream.map { movieEntities ->
            movieEntities.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath
                )
            }
        }
    }

    /**
     * Observes top rated movies from local storage.
     * Returns a flow of movies from the local cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observeTopRatedMovies(): Flow<List<Movie>> {
        val localMoviesPageStream =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(MovieCategory.TOP_RATED.name)

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadTopRatedMoviesNextPage()
        }

        return localMoviesPageStream.map { movieEntities ->
            movieEntities.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath
                )
            }
        }
    }

    /**
     * Observes now playing movies from local storage.
     * Returns a flow of movies from the local cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observeNowPlayingMovies(): Flow<List<Movie>> {
        val localMoviesPageStream =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(MovieCategory.NOW_PLAYING.name)

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadNowPlayingMoviesNextPage()
        }

        return localMoviesPageStream.map { movieEntities ->
            movieEntities.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath
                )
            }
        }
    }

    /**
     * Loads the next page of popular movies from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadPopularMoviesNextPage(): AppResult<Unit> {
        val currentLanguage = getLanguage()
        val paginationState = preferencesManager.getPopularMoviesPaginationState().first()

        if (paginationState.totalPages > 0 && paginationState.currentPage >= paginationState.totalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = paginationState.currentPage + 1

        return when (val result =
            moviesRemoteDataSource.getPopularMoviesPage(nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map {
                    it.asEntity().copy(category = MovieCategory.POPULAR.name)
                }
                moviesLocalDataSource.insertMoviesPage(entities)

                // Save pagination state to DataStore
                preferencesManager.savePopularMoviesPaginationState(
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
     * Loads the next page of top rated movies from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadTopRatedMoviesNextPage(): AppResult<Unit> {
        val currentLanguage = getLanguage()
        val paginationState = preferencesManager.getTopRatedMoviesPaginationState().first()

        if (paginationState.totalPages > 0 && paginationState.currentPage >= paginationState.totalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = paginationState.currentPage + 1

        return when (val result =
            moviesRemoteDataSource.getTopRatedMoviesPage(nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map {
                    it.asEntity().copy(category = MovieCategory.TOP_RATED.name)
                }
                moviesLocalDataSource.insertMoviesPage(entities)

                // Save pagination state to DataStore
                preferencesManager.saveTopRatedMoviesPaginationState(
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
     * Loads the next page of now playing movies from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadNowPlayingMoviesNextPage(): AppResult<Unit> {
        val currentLanguage = getLanguage()
        val paginationState = preferencesManager.getNowPlayingMoviesPaginationState().first()

        if (paginationState.totalPages > 0 && paginationState.currentPage >= paginationState.totalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = paginationState.currentPage + 1

        return when (val result =
            moviesRemoteDataSource.getNowPlayingMoviesPage(nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map {
                    it.asEntity().copy(category = MovieCategory.NOW_PLAYING.name)
                }
                moviesLocalDataSource.insertMoviesPage(entities)

                // Save pagination state to DataStore
                preferencesManager.saveNowPlayingMoviesPaginationState(
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
            val cast = cachedCast.sortedBy { it.order }.map { it.toDomain() }
            return@coroutineScope AppResult.Success(
                cachedMovieDetails.toDomain().copy(trailers = trailers, cast = cast)
            )
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
                profilePath = remoteCastMember.profilePath?.let { "$TMDB_IMAGE_URL$it" },
                order = remoteCastMember.order
            )
        }
        moviesLocalDataSource.replaceCastForMovie(movieId, castEntities)

        // Convert cast to domain for return
        val cast = remoteCast.map { it.toDomain() }

        // 8. Return combined result
        AppResult.Success(detailsEntity.toDomain().copy(trailers = trailers, cast = cast))
    }

    /**
     * Refreshes all movie data by resetting pagination state and loading fresh data
     * for all three categories (popular, top-rated, now-playing) in parallel.
     *
     * @return AppResult<List<Movie>> Either:
     *   - AppResult.Success with the combined refreshed list of all movies if successful
     *   - AppResult.Error if any of the refresh operations failed
     */
    override suspend fun refresh(): AppResult<List<Movie>> {
        // Reset all pagination states in DataStore
        preferencesManager.savePopularMoviesPaginationState(
            PaginationState(currentPage = 0, totalPages = 0)
        )
        preferencesManager.saveTopRatedMoviesPaginationState(
            PaginationState(currentPage = 0, totalPages = 0)
        )
        preferencesManager.saveNowPlayingMoviesPaginationState(
            PaginationState(currentPage = 0, totalPages = 0)
        )

        moviesLocalDataSource.clearAllMovies()

        // Load all three categories in parallel
        val results = awaitAll(
            repositoryScope.async { loadPopularMoviesNextPage() },
            repositoryScope.async { loadTopRatedMoviesNextPage() },
            repositoryScope.async { loadNowPlayingMoviesNextPage() }
        )

        // Check if any failed
        val error = results.firstOrNull { it is AppResult.Error } as? AppResult.Error
        if (error != null) {
            return error
        }

        // All succeeded - return combined list from local storage
        val popularMovies =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(MovieCategory.POPULAR.name).first()
        val topRatedMovies =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(MovieCategory.TOP_RATED.name).first()
        val nowPlayingMovies =
            moviesLocalDataSource.getMoviesByCategoryAsFlow(MovieCategory.NOW_PLAYING.name).first()

        val combinedList = (popularMovies + topRatedMovies + nowPlayingMovies).map {
            Movie(id = it.id, title = it.title, posterPath = it.posterPath)
        }

        return AppResult.Success(combinedList)
    }

    /**
     * Clears all cached movies from local storage and resets pagination state.
     *
     * This function should be called when language changes to ensure movies
     * are re-fetched in the new language.
     */
    override suspend fun clearMovies() {
        // Reset all pagination states in DataStore
        preferencesManager.savePopularMoviesPaginationState(
            PaginationState(currentPage = 0, totalPages = 0)
        )
        preferencesManager.saveTopRatedMoviesPaginationState(
            PaginationState(currentPage = 0, totalPages = 0)
        )
        preferencesManager.saveNowPlayingMoviesPaginationState(
            PaginationState(currentPage = 0, totalPages = 0)
        )
        moviesLocalDataSource.clearAllMovies()
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
