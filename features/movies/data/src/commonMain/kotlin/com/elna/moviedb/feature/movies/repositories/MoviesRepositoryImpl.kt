package com.elna.moviedb.feature.movies.repositories

import com.elna.moviedb.core.datastore.language.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.language.LanguageChangeListener
import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.feature.movies.datasources.MoviesLocalDataSource
import com.elna.moviedb.core.database.model.asEntity
import com.elna.moviedb.core.datastore.pagination.PaginationPreferences
import com.elna.moviedb.core.datastore.pagination.PaginationState
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.dto.credits.toCastMembersOrEmpty
import com.elna.moviedb.core.network.dto.videos.toTrailersOrEmpty
import com.elna.moviedb.feature.movies.datasources.MoviesRemoteDataSource
import com.elna.moviedb.feature.movies.mappers.asEntity
import com.elna.moviedb.feature.movies.mappers.toDomain
import com.elna.moviedb.feature.movies.mappers.toTmdbPath
import com.elna.moviedb.feature.movies.model.Movie
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.MovieDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Per-page multiplier for computing a movie's absolute [MovieEntity.position] within a category. */
private const val PAGE_ORDER_STRIDE = 1000

/**
 * Implementation of MoviesRepository that manages movie data from remote API and local cache.
 *
 * This repository uses category abstraction.
 * New movie categories can be added to [com.elna.moviedb.feature.movies.model.MovieCategory] enum without modifying this class.
 *
 * Movie details follow an offline-first policy: cached data is returned when present,
 * otherwise it is fetched from the network and persisted before returning.
 *
 * This repository implements LanguageChangeListener and self-registers with the coordinator
 * during initialization, ensuring it's always properly set up to respond to language changes.
 *
 * @param remoteDataSource Remote data source for fetching movies from API
 * @param localDataSource Local data source for caching movies in database
 * @param paginationPreferences Manager for pagination state
 * @param languageProvider Provider for formatted language strings
 * @param languageChangeCoordinator Coordinator for language change notifications
 */
class MoviesRepositoryImpl(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource,
    private val paginationPreferences: PaginationPreferences,
    private val languageProvider: LanguageProvider,
    languageChangeCoordinator: LanguageChangeCoordinator,
) : MoviesRepository, LanguageChangeListener {

    init {
        // Self-register with coordinator during initialization
        // Ensures repository is always properly set up to receive language change notifications
        languageChangeCoordinator.registerListener(this)
    }

    // One lock per category. Pre-populated from the fixed enum so the map is immutable after
    // construction (no racy getOrPut), and distinct categories use distinct locks so their
    // loads never block each other.
    //
    // Concurrency: same-category loads are serialized by these locks so the
    // check-then-fetch-then-write sequence in loadMoviesNextPage() can't interleave for a given
    // category — read a stale page, fetch it twice, and skip the next one — regardless of the
    // caller's dispatcher. This keeps the repository self-protecting rather than relying on the
    // ViewModel's per-category loading guard. clearAndReload() acquires *all* of these locks (see
    // withAllCategoryLocks) so its cache wipe can't interleave with an in-flight load; its
    // per-category reloads still run in parallel, just under the already-held locks.
    private val categoryLocks: Map<MovieCategory, Mutex> =
        MovieCategory.entries.associateWith { Mutex() }

    /**
     * Observes movies for a specific category from local storage.
     *
     * Passive query: returns the cached stream and performs no side effects. The
     * caller (ViewModel) decides when to trigger an initial/refresh load via
     * [loadMoviesNextPage].
     */
    override fun observeMovies(category: MovieCategory): Flow<List<Movie>> =
        localDataSource.getMoviesByCategoryAsFlow(category)
            .map { movieEntities -> movieEntities.map { it.toDomain() } }

    /**
     * Loads the next page of movies for a specific category from the remote API.
     *
     * @param category The movie category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> =
        categoryLocks.getValue(category).withLock {
            loadMoviesNextPageUnlocked(category)
        }

    /**
     * Loads the next page for [category] **without** acquiring its lock.
     *
     * The caller must already hold the relevant lock(s): [loadMoviesNextPage] holds the
     * single category lock, and [clearAndReload] holds every lock via [withAllCategoryLocks].
     * Splitting the body out this way lets clearAndReload reload under the locks it already
     * holds without re-entering (Kotlin's [Mutex] is non-reentrant and would deadlock).
     */
    private suspend fun loadMoviesNextPageUnlocked(category: MovieCategory): AppResult<Unit> {
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
                // Absolute rank = page offset + index within the page, so ordering is stable
                // across pages. PAGE_ORDER_STRIDE is comfortable headroom over TMDB's 20-item
                // pages; index never reaches it, so positions never overlap between pages.
                val entities = result.data.results.mapIndexed { index, remoteMovie ->
                    remoteMovie.asEntity(category, position = nextPage * PAGE_ORDER_STRIDE + index)
                }
                localDataSource.insertMoviesPage(entities)

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
     * Runs [block] while holding every category lock, acquired in a fixed (enum) order.
     *
     * [clearAndReload] uses this so its cache wipe can't interleave with an in-flight
     * single-category load: a load that had already read its pagination page and was awaiting
     * the network would otherwise resume *after* the wipe and re-persist that now-stale page,
     * leaving the earlier pages permanently missing (a gap that only a later refresh heals).
     * Holding all locks blocks such a load until the wipe-and-reload finishes; the parallel
     * reloads inside call [loadMoviesNextPageUnlocked] (the locks are already held), so they
     * neither deadlock nor lose their parallelism.
     */
    private suspend fun <T> withAllCategoryLocks(block: suspend () -> T): T {
        val locks = categoryLocks.values.toList()
        suspend fun acquireFrom(index: Int): T =
            if (index == locks.size) block()
            else locks[index].withLock { acquireFrom(index + 1) }
        return acquireFrom(0)
    }

    /**
     * Retrieves detailed information for a specific movie (offline-first).
     *
     * 1. Returns cached details immediately on a cache hit.
     * 2. On a cache miss, fetches from the remote API in parallel (details + videos + cast).
     * 3. Persists the fetched data to cache before returning it.
     *
     * @param movieId The unique identifier of the movie to retrieve
     * @return AppResult<MovieDetails> Success with movie details or Error if fetch failed
     */
    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        fetchMovieDetailsFromCache(movieId)?.let { return AppResult.Success(it) }

        return fetchMovieDetailsFromNetwork(movieId)
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
     *
     * Persists to the offline-first cache only when the result is *complete* — see the
     * inline note for why a partial result must not be cached.
     */
    private suspend fun fetchMovieDetailsFromNetwork(movieId: Int): AppResult<MovieDetails> =
        coroutineScope {
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

            val trailers = videosResult.toTrailersOrEmpty()
            val cast = creditsResult.toCastMembersOrEmpty()

            val movieDetails = details.toDomain().copy(
                trailers = trailers,
                cast = cast
            )

            // Offline-first, but persist only a *complete* result. Videos/cast degrade to
            // empty on failure; since a later visit is served straight from cache (a hit on
            // the details row), caching that empty data would hide cast/trailers permanently
            // after a transient hiccup. When an optional fetch failed, return the partial
            // result for this session only and re-fetch on the next visit.
            val isComplete =
                videosResult is AppResult.Success && creditsResult is AppResult.Success
            if (isComplete) {
                saveMovieDetailsToCache(movieId, movieDetails)
            }

            AppResult.Success(movieDetails)
        }

    /**
     * Saves movie details to local cache.
     *
     * Details, videos, and cast are persisted in a single transaction (see
     * [com.elna.moviedb.core.database.MovieDetailsDao.insertMovieDetailsWithRelations]):
     * since the cache-hit check keys on the details row alone, a partial write would
     * otherwise serve a "complete" hit with missing relations on the next visit.
     */
    private suspend fun saveMovieDetailsToCache(movieId: Int, movieDetails: MovieDetails) {
        val videoEntities = (movieDetails.trailers ?: emptyList()).map { it.asEntity(movieId) }
        val castEntities = (movieDetails.cast ?: emptyList()).map { it.asEntity(movieId) }

        localDataSource.saveMovieDetailsWithRelations(
            details = movieDetails.asEntity(),
            videos = videoEntities,
            cast = castEntities
        )
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
     * This method automatically handles all categories defined in [com.elna.moviedb.feature.movies.model.MovieCategory] enum.
     */
    override suspend fun clearAndReload(): AppResult<Unit> = withAllCategoryLocks {
        // Hold every category lock across the whole wipe-and-reload so a concurrent
        // single-category load can't interleave and re-persist a stale page (see
        // withAllCategoryLocks).

        // Clear all pagination state and local data
        paginationPreferences.clearAllPaginationState()

        // Clear all movie-related caches using segregated methods
        // Each interface is responsible for clearing only its own data
        localDataSource.clearMoviesList()
        localDataSource.clearMovieDetails()
        localDataSource.clearAllVideos()
        localDataSource.clearAllCast()

        // Load all categories in parallel and await the outcomes — the cache was just
        // wiped, so a swallowed failure here would leave the screen empty with no error.
        // The unlocked variant is used because the locks are already held.
        val results = coroutineScope {
            MovieCategory.entries.map { category ->
                async { loadMoviesNextPageUnlocked(category) }
            }.awaitAll()
        }

        // Partial success still yields content; only report an error when all failed.
        results.firstOrNull { it is AppResult.Success } ?: results.first()
    }
}