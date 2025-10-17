package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.data.util.toFullImageUrl
import com.elna.moviedb.core.datastore.AppSettingsPreferences
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.model.tv_shows.toDomain
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

/**
 * Implementation of TvShowsRepository that manages TV show data from remote API.
 *
 * This repository follows the Open/Closed Principle by using category abstraction.
 * New TV show categories can be added to [TvShowCategory] enum without modifying this class.
 *
 * **Note:** This repository uses in-memory storage (MutableStateFlow) rather than
 * local database caching. TV shows are fetched from the API and held in memory
 * for the duration of the app session. For persistent offline-first storage,
 * see MoviesRepositoryImpl which uses Room database.
 *
 * This repository provides data access operations only. Language change coordination
 * is handled separately by [com.elna.moviedb.core.data.LanguageChangeCoordinator].
 *
 * @param tvShowsRemoteDataSource Remote data source for fetching TV shows from API
 * @param appSettingsPreferences Manager for accessing app settings (language)
 */
class TvShowRepositoryImpl(
    private val tvShowsRemoteDataSource: TvShowsRemoteDataSource,
    private val appSettingsPreferences: AppSettingsPreferences,
) : TvShowsRepository {

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
     * This method follows the Open/Closed Principle - new categories can be
     * added to TvShowCategory enum without modifying this method.
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
     * This method follows the Open/Closed Principle - new categories can be
     * added to TvShowCategory enum without modifying this method.
     *
     * @param category The TV show category to load
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadTvShowsNextPage(category: TvShowCategory): AppResult<Unit> {
        val currentPage = currentPages[category] ?: 0
        val totalPage = totalPages[category] ?: 0

        if (totalPage > 0 && currentPage >= totalPage) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = currentPage + 1

        return when (val result =
            tvShowsRemoteDataSource.fetchTvShowsPage(category.apiPath, nextPage, getLanguage())) {
            is AppResult.Success -> {
                totalPages[category] = result.data.totalPages
                val newTvShows = result.data.results.map { remoteTvShow ->
                    remoteTvShow.toDomain().copy(
                        posterPath = remoteTvShow.posterPath.toFullImageUrl()
                    )
                }

                val flow = getFlowForCategory(category)
                flow.value = flow.value + newTvShows
                currentPages[category] = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Clears all cached TV shows and reloads initial pages for all categories.
     *
     * This method is called by [com.elna.moviedb.core.data.LanguageChangeCoordinator] when the app language changes.
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
        val language = getLanguage()

        // Fetch details, videos, and credits in parallel
        val detailsDeferred =
            async { tvShowsRemoteDataSource.getTvShowDetails(tvShowId, language) }
        val videosDeferred =
            async { tvShowsRemoteDataSource.getTvShowVideos(tvShowId, language) }
        val creditsDeferred =
            async { tvShowsRemoteDataSource.getTvShowCredits(tvShowId, language) }

        val detailsResult = detailsDeferred.await()
        val videosResult = videosDeferred.await()
        val creditsResult = creditsDeferred.await()

        // Extract details or return error
        val details = when (detailsResult) {
            is AppResult.Success -> detailsResult.data
            is AppResult.Error -> return@coroutineScope detailsResult
        }

        // Map videos to domain and filter for trailers/teasers
        val trailers = when (videosResult) {
            is AppResult.Success -> {
                videosResult.data.results
                    .filter { it.type == "Trailer" || it.type == "Teaser" }
                    .sortedWith(compareByDescending<RemoteVideo> { it.official }
                        .thenByDescending { it.publishedAt })
                    .map { it.toDomain() }
            }

            is AppResult.Error -> emptyList() // Videos are optional, don't fail if they error
        }

        // Map cast to domain and sort by order
        val cast = when (creditsResult) {
            is AppResult.Success -> {
                creditsResult.data.cast
                    ?.sortedBy { it.order }
                    ?.map { remoteCastMember ->
                        remoteCastMember.toDomain().copy(
                            profilePath = remoteCastMember.profilePath.toFullImageUrl()
                        )
                    }
                    ?: emptyList()
            }

            is AppResult.Error -> emptyList() // Cast is optional, don't fail if they error
        }

        // Combine details with trailers and cast - add URL concatenation for poster and backdrop
        val tvShowDetails = details.toDomain().copy(
            posterPath = details.posterPath.toFullImageUrl(),
            backdropPath = details.backdropPath.toFullImageUrl(),
            trailers = trailers,
            cast = cast
        )
        AppResult.Success(tvShowDetails)
    }

    private suspend fun getLanguage(): String {
        val languageCode = appSettingsPreferences.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
