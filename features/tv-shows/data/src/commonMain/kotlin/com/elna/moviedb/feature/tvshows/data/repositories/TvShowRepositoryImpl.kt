package com.elna.moviedb.feature.tvshows.data.repositories

import com.elna.moviedb.core.datastore.language.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.language.LanguageChangeListener
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShow
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.core.network.dto.credits.toCastMembersOrEmpty
import com.elna.moviedb.core.network.dto.videos.toTrailersOrEmpty
import com.elna.moviedb.feature.tvshows.data.datasources.TvShowsRemoteService
import com.elna.moviedb.feature.tvshows.data.mappers.toDomain
import com.elna.moviedb.feature.tvshows.data.mappers.toTmdbPath
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementation of TvShowsRepository that manages TV show data from remote API.
 *
 * This repository uses category abstraction.
 * New TV show categories can be added to [TvShowCategory] enum without modifying this class.
 *
 * **Note:** This repository uses in-memory storage (MutableStateFlow) rather than
 * local database caching. TV shows are fetched from the API and held in memory
 * for the duration of the app session. For persistent offline-first storage,
 * see MoviesRepositoryImpl which uses Room database.
 *
 * This repository implements LanguageChangeListener and self-registers with the coordinator
 * during initialization, ensuring it's always properly set up to respond to language changes.
 *
 * @param remoteDataSource Remote data source for fetching TV shows from API
 * @param languageProvider Provider for formatted language strings
 * @param languageChangeCoordinator Coordinator for language change notifications
 */
class TvShowRepositoryImpl(
    private val remoteDataSource: TvShowsRemoteService,
    private val languageProvider: LanguageProvider,
    languageChangeCoordinator: LanguageChangeCoordinator,
) : TvShowsRepository, LanguageChangeListener {

    // Category-based pagination state using Maps for scalability.
    //
    // Concurrency: same-category loads are serialized by `categoryLocks` (below), so the
    // check-then-fetch-then-write sequence in loadTvShowsNextPage() can't interleave for a
    // given category and read a stale page, fetch it twice, and skip the next one — regardless
    // of the caller's dispatcher. clearAndReload() acquires *all* of these locks (see
    // withAllCategoryLocks) so its reset can't interleave with an in-flight load; its
    // per-category reloads still run in parallel, just under the already-held locks. The
    // backing maps below stay correct because they are pre-populated in init and only mutated
    // while holding their respective category lock.
    private val currentPages = mutableMapOf<TvShowCategory, Int>()
    private val totalPages = mutableMapOf<TvShowCategory, Int>()
    private val tvShowsFlows = mutableMapOf<TvShowCategory, MutableStateFlow<List<TvShow>>>()

    init {
        // Pre-populate maps to avoid racy getOrPut calls during concurrent access.
        TvShowCategory.entries.forEach { category ->
            currentPages[category] = 0
            totalPages[category] = 0
            tvShowsFlows[category] = MutableStateFlow(emptyList())
        }

        // Self-register with coordinator during initialization
        // Ensures repository is always properly set up to receive language change notifications
        languageChangeCoordinator.registerListener(this)
    }

    // One lock per category. Pre-populated from the fixed enum so the map is immutable after
    // construction (no racy getOrPut), and distinct categories use distinct locks so their
    // loads never block each other.
    private val categoryLocks: Map<TvShowCategory, Mutex> =
        TvShowCategory.entries.associateWith { Mutex() }

    /**
     * Observes TV shows for a specific category from in-memory storage.
     *
     * Passive query: returns the in-memory stream and performs no side effects. The
     * caller (ViewModel) decides when to trigger an initial/refresh load via
     * [loadTvShowsNextPage].
     *
     * @param category The TV show category to observe
     * @return Flow emitting list of TV shows for the category
     */
    override fun observeTvShows(category: TvShowCategory): Flow<List<TvShow>> =
        tvShowsFlows.getValue(category)

    /**
     * Loads the next page of TV shows for a specific category from the remote API.
     *
     * Ensures distinct TV shows by ID when adding new pages to prevent duplicates.
     *
     * @param category The TV show category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadTvShowsNextPage(category: TvShowCategory): AppResult<Unit> =
        categoryLocks.getValue(category).withLock {
            loadTvShowsNextPageUnlocked(category)
        }

    /**
     * Loads the next page for [category] **without** acquiring its lock.
     *
     * The caller must already hold the relevant lock(s): [loadTvShowsNextPage] holds the
     * single category lock, and [clearAndReload] holds every lock via [withAllCategoryLocks].
     * Splitting the body out this way lets clearAndReload reload under the locks it already
     * holds without re-entering (Kotlin's [Mutex] is non-reentrant and would deadlock).
     */
    private suspend fun loadTvShowsNextPageUnlocked(category: TvShowCategory): AppResult<Unit> {
        val currentPage = currentPages[category] ?: 0
        val totalPage = totalPages[category] ?: 0

        if (totalPage in 1..currentPage) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = currentPage + 1

        return when (val result =
            remoteDataSource.fetchTvShowsPage(category.toTmdbPath(), nextPage, languageProvider.getCurrentLanguage())) {
            is AppResult.Success -> {
                totalPages[category] = result.data.totalPages
                val newTvShows = result.data.results.map { remoteTvShow ->
                    remoteTvShow.toDomain()
                }

                val flow = tvShowsFlows.getValue(category)
                flow.value = (flow.value + newTvShows).distinctBy { it.id }
                currentPages[category] = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Runs [block] while holding every category lock, acquired in a fixed (enum) order.
     *
     * [clearAndReload] uses this so its cache wipe can't interleave with an in-flight
     * single-category load: a load that had already read its page counters and was awaiting
     * the network would otherwise resume *after* the wipe and re-append that now-stale page,
     * leaving the earlier pages missing. Holding all locks blocks such a load until the
     * wipe-and-reload finishes; the parallel reloads inside call [loadTvShowsNextPageUnlocked]
     * (the locks are already held), so they neither deadlock nor lose their parallelism.
     */
    private suspend fun <T> withAllCategoryLocks(block: suspend () -> T): T {
        val locks = categoryLocks.values.toList()
        suspend fun acquireFrom(index: Int): T =
            if (index == locks.size) block()
            else locks[index].withLock { acquireFrom(index + 1) }
        return acquireFrom(0)
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
     * Clears all cached TV shows and reloads initial pages for all categories.
     *
     * This method is called when the app language changes via onLanguageChanged().
     * It clears the in-memory cache and fetches fresh data in the new language.
     */
    override suspend fun clearAndReload(): AppResult<Unit> = withAllCategoryLocks {
        // Hold every category lock across the whole wipe-and-reload so a concurrent
        // single-category load can't interleave and re-append a stale page (see
        // withAllCategoryLocks).

        // Clear all pagination state and cached data for all categories
        currentPages.clear()
        totalPages.clear()
        tvShowsFlows.values.forEach { it.value = emptyList() }

        // Reload all categories in parallel and await the outcomes so failures aren't
        // swallowed (the in-memory cache was just cleared). The unlocked variant is used
        // because the locks are already held.
        val results = coroutineScope {
            TvShowCategory.entries.map { category ->
                async { loadTvShowsNextPageUnlocked(category) }
            }.awaitAll()
        }

        // Partial success still yields content; only report an error when all failed.
        results.firstOrNull { it is AppResult.Success } ?: results.first()
    }

    override suspend fun getTvShowDetails(tvShowId: Int): AppResult<TvShowDetails> =
        coroutineScope {
            val language = languageProvider.getCurrentLanguage()

            // Fetch details, videos, and credits in parallel
            val detailsDeferred =
                async { remoteDataSource.getTvShowDetails(tvShowId, language) }
            val videosDeferred =
                async { remoteDataSource.getTvShowVideos(tvShowId, language) }
            val creditsDeferred =
                async { remoteDataSource.getTvShowCredits(tvShowId, language) }

            val detailsResult = detailsDeferred.await()
            val videosResult = videosDeferred.await()
            val creditsResult = creditsDeferred.await()

            // Extract details or return error
            val details = when (detailsResult) {
                is AppResult.Success -> detailsResult.data
                is AppResult.Error -> return@coroutineScope detailsResult
            }

            val trailers = videosResult.toTrailersOrEmpty()
            val cast = creditsResult.toCastMembersOrEmpty()

            // Combine details with trailers and cast
            val tvShowDetails = details.toDomain().copy(
                trailers = trailers,
                cast = cast
            )
            AppResult.Success(tvShowDetails)
        }
}
