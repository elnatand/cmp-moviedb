package com.elna.moviedb.core.data.movies


import com.elna.moviedb.core.data.LanguageChangeCoordinator
import com.elna.moviedb.core.data.LanguageChangeListener
import com.elna.moviedb.core.data.model.asEntity
import com.elna.moviedb.core.data.strategy.CachingStrategy
import com.elna.moviedb.core.data.util.LanguageProvider
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.database.model.CastMemberEntity
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.asEntity
import com.elna.moviedb.core.datastore.PaginationPreferences
import com.elna.moviedb.core.datastore.model.PaginationState
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.CastMember
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.core.model.Video
import com.elna.moviedb.core.network.MoviesRemoteDataSource
import com.elna.moviedb.core.network.mapper.toTmdbPath
import com.elna.moviedb.core.network.model.movies.RemoteMovieCredits
import com.elna.moviedb.core.network.model.movies.toDomain
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.RemoteVideoResponse
import com.elna.moviedb.core.network.model.videos.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementation of MoviesRepository that manages movie data from remote API and local cache.
 *
 * This repository uses category abstraction.
 * New movie categories can be added to [MovieCategory] enum without modifying this class.
 *
 * This repository uses the Strategy Pattern for caching, allowing different caching
 * behaviors to be injected without modifying repository code.
 *
 * This repository implements LanguageChangeListener and self-registers with the coordinator
 * during initialization, ensuring it's always properly set up to respond to language changes.
 *
 * @param remoteDataSource Remote data source for fetching movies from API
 * @param localDataSource Local data source for caching movies in database
 * @param paginationPreferences Manager for pagination state
 * @param languageProvider Provider for formatted language strings
 * @param cachingStrategy Strategy for cache/network coordination (default: offline-first)
 * @param languageChangeCoordinator Coordinator for language change notifications
 */
class MoviesRepositoryImpl(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
    private val paginationPreferences: PaginationPreferences,
    private val languageProvider: LanguageProvider,
    private val cachingStrategy: CachingStrategy,
    languageChangeCoordinator: LanguageChangeCoordinator,
) : MoviesRepository, LanguageChangeListener {

    init {
        // Self-register with coordinator during initialization
        // Ensures repository is always properly set up to receive language change notifications
        languageChangeCoordinator.registerListener(this)
    }

    /**
     * Observes movies for a specific category from local storage.
     *
     * Returns a flow of movies from the local cache. Automatically triggers
     * initial load if cache is empty for the given category.
     */
    override suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>> {
        // Use type-safe category enum directly
        val localMoviesPageStream =
            localDataSource.getMoviesByCategoryAsFlow(category)

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadMoviesNextPage(category)
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
     * Loads the next page of movies for a specific category from the remote API.
     *
     * @param category The movie category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
        val currentLanguage = languageProvider.getCurrentLanguage()
        val paginationState = paginationPreferences.getPaginationState(category.name).first()

        if (paginationState.totalPages > 0 && paginationState.currentPage >= paginationState.totalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = paginationState.currentPage + 1

        return when (val result =
            remoteDataSource.fetchMoviesPage(category.toTmdbPath(), nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map {
                    it.asEntity().copy(category = category.name)
                }
                localDataSource.insertMoviesPage(entities)

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
     * Retrieves detailed information for a specific movie using caching strategy.
     *
     * This function uses the injected CachingStrategy (typically offline-first) to:
     * 1. Check local cache for movie details
     * 2. On cache miss, fetch from remote API in parallel (details + videos + cast)
     * 3. Save fetched data to cache
     * 4. Return movie details with trailers and cast
     *
     * @param movieId The unique identifier of the movie to retrieve
     * @return AppResult<MovieDetails> Success with movie details or Error if fetch failed
     */
    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        return cachingStrategy.execute(
            fetchFromCache = {
                fetchMovieDetailsFromCache(movieId)
            },
            fetchFromNetwork = {
                fetchMovieDetailsFromNetwork(movieId)
            },
            saveToCache = { movieDetails ->
                saveMovieDetailsToCache(movieId, movieDetails)
            }
        )
    }

    /**
     * Fetches movie details from local cache.
     * Returns null if not cached or incomplete data.
     */
    private suspend fun fetchMovieDetailsFromCache(movieId: Int): MovieDetails? {
        val cachedMovieDetails = localDataSource.getMovieDetails(movieId) ?: return null
        val cachedVideos = localDataSource.getVideosForMovie(movieId)
        val cachedCast = localDataSource.getCastForMovie(movieId)

        return cachedMovieDetails.toDomain().copy(
            trailers = cachedVideos.map { it.toDomain() },
            cast = cachedCast.sortedBy { it.order }.map { it.toDomain() }
        )
    }

    /**
     * Fetches movie details from network, making parallel API calls.
     * Implements graceful degradation for optional data (videos, cast).
     */
    private suspend fun fetchMovieDetailsFromNetwork(movieId: Int): AppResult<MovieDetails> = coroutineScope {
        val language = languageProvider.getCurrentLanguage()

        // Fetch all data in parallel for performance
        val detailsDeferred = async { remoteDataSource.getMovieDetails(movieId, language) }
        val videosDeferred = async { remoteDataSource.getMovieVideos(movieId, language) }
        val creditsDeferred = async { remoteDataSource.getMovieCredits(movieId, language) }

        // Details are required - fail if they don't load
        val detailsResult = detailsDeferred.await()
        val details = when (detailsResult) {
            is AppResult.Success -> detailsResult.data
            is AppResult.Error -> return@coroutineScope detailsResult
        }

        // Videos and cast are optional - graceful degradation
        val videosResult = videosDeferred.await()
        val creditsResult = creditsDeferred.await()

        val trailers = processVideosResult(videosResult)
        val cast = processCreditsResult(creditsResult)

        // Convert to entity then to domain (to apply mapping logic)
        val detailsEntity = details.asEntity()
        val detailsDomain = detailsEntity.toDomain()

        // Return domain model with trailers and cast
        AppResult.Success(
            detailsDomain.copy(
                trailers = trailers,
                cast = cast
            )
        )
    }

    /**
     * Processes video results, filtering for trailers and teasers.
     * Returns empty list on error (graceful degradation).
     */
    private fun processVideosResult(videosResult: AppResult<RemoteVideoResponse>)
        : List<Video> {
        return when (videosResult) {
            is AppResult.Success -> {
                videosResult.data.results
                    .filter { it.type == "Trailer" || it.type == "Teaser" }
                    .sortedWith(
                        compareByDescending<RemoteVideo> { it.official }
                            .thenByDescending { it.publishedAt }
                    )
                    .map { it.toDomain() }
            }
            is AppResult.Error -> emptyList()
        }
    }

    /**
     * Processes cast credits results, sorting by order.
     * Returns empty list on error (graceful degradation).
     */
    private fun processCreditsResult(creditsResult: AppResult<RemoteMovieCredits>)
        : List<CastMember> {
        return when (creditsResult) {
            is AppResult.Success -> {
                creditsResult.data.cast
                    ?.sortedBy { it.order }
                    ?.map { it.toDomain() }
                    ?: emptyList()
            }
            is AppResult.Error -> emptyList()
        }
    }

    /**
     * Saves movie details to local cache.
     * Saves details, videos, and cast in separate operations.
     */
    private suspend fun saveMovieDetailsToCache(movieId: Int, movieDetails: MovieDetails) {
        // Save main details
        val detailsEntity = movieDetails.run {
            MovieDetailsEntity(
                id = id,
                title = title,
                overview = overview,
                posterPath = posterPath,
                backdropPath = backdropPath,
                releaseDate = releaseDate,
                runtime = runtime,
                voteAverage = voteAverage,
                voteCount = voteCount,
                adult = adult,
                budget = budget,
                revenue = revenue,
                homepage = homepage,
                imdbId = imdbId,
                originalLanguage = originalLanguage,
                originalTitle = originalTitle,
                popularity = popularity,
                status = status,
                tagline = tagline,
                genres = genres?.joinToString(","),
                productionCompanies = productionCompanies?.joinToString(","),
                productionCountries = productionCountries?.joinToString(","),
                spokenLanguages = spokenLanguages?.joinToString(",")
            )
        }
        localDataSource.insertMovieDetails(detailsEntity)

        // Save videos
        localDataSource.deleteVideosForMovie(movieId)
        val trailersList = movieDetails.trailers ?: emptyList()
        if (trailersList.isNotEmpty()) {
            val videoEntities = trailersList.map { it.asEntity(movieId) }
            localDataSource.insertVideos(videoEntities)
        }

        // Save cast
        val castList = movieDetails.cast ?: emptyList()
        val castEntities = castList.map { castMember ->
            CastMemberEntity(
                movieId = movieId,
                personId = castMember.id,
                name = castMember.name,
                character = castMember.character,
                profilePath = castMember.profilePath,
                order = castMember.order
            )
        }
        localDataSource.replaceCastForMovie(movieId, castEntities)
    }

    /**
     * Responds to language changes via the Observer Pattern.
     * Automatically called by LanguageChangeCoordinator when language changes.
     *
     * Implementation of LanguageChangeListener interface - delegates to clearAndReload().
     */
    override suspend fun onLanguageChanged() {
        clearAndReload()
    }

    /**
     * Clears all cached movies and reloads initial pages for all categories.
     *
     * This method is called when the app language changes via onLanguageChanged().
     * It clears the local cache and fetches fresh data in the new language.
     *
     * This method automatically handles all categories defined in [MovieCategory] enum.
     */
    override suspend fun clearAndReload() {
        // Clear all pagination state and local data
        paginationPreferences.clearAllPaginationState()

        // Clear all movie-related caches using segregated methods
        // Each interface is responsible for clearing only its own data
        localDataSource.clearMoviesList()
        localDataSource.clearMovieDetails()
        localDataSource.clearAllVideos()
        localDataSource.clearAllCast()

        // Load all categories in parallel
        coroutineScope {
            MovieCategory.entries.forEach { category ->
                async { loadMoviesNextPage(category) }
            }
        }
    }
}
