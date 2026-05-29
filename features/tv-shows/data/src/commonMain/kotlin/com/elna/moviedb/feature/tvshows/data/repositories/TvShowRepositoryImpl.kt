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

    init {
        // Self-register with coordinator during initialization
        // Ensures repository is always properly set up to receive language change notifications
        languageChangeCoordinator.registerListener(this)
    }

    // Category-based pagination state using Maps for scalability.
    //
    // Concurrency: these plain mutable maps carry no synchronization. Every mutation path —
    // observeTvShows() (getOrPut), loadTvShowsNextPage(), and clearAndReload() — runs on the
    // main dispatcher: the ViewModel drives loads from viewModelScope (main) and the
    // LanguageChangeCoordinator dispatches onLanguageChanged() from its own main-confined
    // scope. The remote fetch hops to IO internally (inside TmdbApiClient) but returns to the
    // caller's main context before any map is touched, so accesses are serialized on a single
    // thread and never race. If a future caller mutates these off the main dispatcher, this
    // must move to a Mutex.
    private val currentPages = mutableMapOf<TvShowCategory, Int>()
    private val totalPages = mutableMapOf<TvShowCategory, Int>()
    private val tvShowsFlows = mutableMapOf<TvShowCategory, MutableStateFlow<List<TvShow>>>()

    /**
     * Helper function to get or create a StateFlow for a specific category.
     * Ensures lazy initialization of category flows.
     */
    private fun getFlowForCategory(category: TvShowCategory): MutableStateFlow<List<TvShow>> {
        return tvShowsFlows.getOrPut(category) { MutableStateFlow(emptyList()) }
    }

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
        getFlowForCategory(category)

    /**
     * Loads the next page of TV shows for a specific category from the remote API.
     *
     * Ensures distinct TV shows by ID when adding new pages to prevent duplicates.
     *
     * @param category The TV show category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadTvShowsNextPage(category: TvShowCategory): AppResult<Unit> {
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

                val flow = getFlowForCategory(category)
                flow.value = (flow.value + newTvShows).distinctBy { it.id }
                currentPages[category] = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
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
    override suspend fun clearAndReload(): AppResult<Unit> {
        // Clear all pagination state and cached data for all categories
        currentPages.clear()
        totalPages.clear()
        tvShowsFlows.values.forEach { it.value = emptyList() }

        // Reload all categories in parallel and await the outcomes so failures aren't
        // swallowed (the in-memory cache was just cleared).
        val results = coroutineScope {
            TvShowCategory.entries.map { category ->
                async { loadTvShowsNextPage(category) }
            }.awaitAll()
        }

        // Partial success still yields content; only report an error when all failed.
        return results.firstOrNull { it is AppResult.Success } ?: results.first()
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
