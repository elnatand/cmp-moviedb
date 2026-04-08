package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.data.LanguageChangeCoordinator
import com.elna.moviedb.core.data.LanguageChangeListener
import com.elna.moviedb.core.data.util.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.ContentInfo
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.utils.extractUsRating
import com.elna.moviedb.core.network.utils.toContentDescriptors
import com.elna.moviedb.core.network.mapper.toTmdbPath
import com.elna.moviedb.core.network.model.tv_shows.toDomain
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

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
    private val remoteDataSource: TvShowsRemoteDataSource,
    private val languageProvider: LanguageProvider,
    languageChangeCoordinator: LanguageChangeCoordinator,
) : TvShowsRepository, LanguageChangeListener {

    init {
        // Self-register with coordinator during initialization
        // Ensures repository is always properly set up to receive language change notifications
        languageChangeCoordinator.registerListener(this)
    }

    // Category-based pagination state using Maps for scalability
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
     * Returns a flow of TV shows from the in-memory cache. Automatically triggers
     * initial load if cache is empty for the given category.
     *
     * @param category The TV show category to observe
     * @return Flow emitting list of TV shows for the category
     */
    override suspend fun observeTvShows(category: TvShowCategory): Flow<List<TvShow>> {
        val flow = getFlowForCategory(category)

        if (flow.value.isEmpty()) {
            loadTvShowsNextPage(category)
        }

        return flow
    }

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
    override suspend fun clearAndReload() {
        // Clear all pagination state and cached data for all categories
        currentPages.clear()
        totalPages.clear()
        tvShowsFlows.values.forEach { it.value = emptyList() }

        // Reload all categories in parallel
        coroutineScope {
            TvShowCategory.entries.forEach { category ->
                async { loadTvShowsNextPage(category) }
            }
        }
    }

    override suspend fun getTvShowDetails(tvShowId: Int): AppResult<TvShowDetails> = coroutineScope {
        val language = languageProvider.getCurrentLanguage()

        // Fetch all data in parallel
        val detailsDeferred = async { remoteDataSource.getTvShowDetails(tvShowId, language) }
        val videosDeferred = async { remoteDataSource.getTvShowVideos(tvShowId, language) }
        val creditsDeferred = async { remoteDataSource.getTvShowCredits(tvShowId, language) }
        val contentRatingsDeferred = async { remoteDataSource.getTvShowContentRatings(tvShowId) }
        val keywordsDeferred = async { remoteDataSource.getTvShowKeywords(tvShowId) }

        val detailsResult = detailsDeferred.await()

        // Extract details or return error
        val details = when (detailsResult) {
            is AppResult.Success -> detailsResult.data
            is AppResult.Error -> return@coroutineScope detailsResult
        }

        val trailers = when (val r = videosDeferred.await()) {
            is AppResult.Success -> r.data.results
                .filter { it.type == "Trailer" || it.type == "Teaser" }
                .sortedWith(compareByDescending<RemoteVideo> { it.official }
                    .thenByDescending { it.publishedAt })
                .map { it.toDomain() }
            is AppResult.Error -> emptyList()
        }

        val cast = when (val r = creditsDeferred.await()) {
            is AppResult.Success -> r.data.cast
                ?.sortedBy { it.order }
                ?.map { it.toDomain() }
                ?: emptyList()
            is AppResult.Error -> emptyList()
        }

        val ageRating = when (val r = contentRatingsDeferred.await()) {
            is AppResult.Success -> r.data.extractUsRating()
            is AppResult.Error -> null
        }
        val descriptors = when (val r = keywordsDeferred.await()) {
            is AppResult.Success -> r.data.results.map { it.name }.toContentDescriptors()
            is AppResult.Error -> emptyList()
        }
        val contentInfo = if (ageRating != null || descriptors.isNotEmpty()) {
            ContentInfo(ageRating = ageRating, contentDescriptors = descriptors)
        } else null

        AppResult.Success(
            details.toDomain().copy(trailers = trailers, cast = cast, contentInfo = contentInfo)
        )
    }
}
