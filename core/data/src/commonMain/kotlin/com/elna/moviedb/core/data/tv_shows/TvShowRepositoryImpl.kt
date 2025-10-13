package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.model.tv_shows.toDomain
import com.elna.moviedb.core.network.model.videos.RemoteVideo
import com.elna.moviedb.core.network.model.videos.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Implementation of TvShowsRepository that manages TV show data from remote API.
 *
 * **Note:** This repository uses in-memory storage (MutableStateFlow) rather than
 * local database caching. TV shows are fetched from the API and held in memory
 * for the duration of the app session. For persistent offline-first storage,
 * see MoviesRepositoryImpl which uses Room database.
 *
 * **Lifecycle:** This repository is an application-scoped singleton managed by Koin DI.
 * The [repositoryScope] is never cancelled and lives for the entire application lifetime.
 * This is intentional as the repository maintains app-wide state and language change observers.
 *
 * @param tvShowsRemoteDataSource Remote data source for fetching TV shows from API
 * @param preferencesManager Manager for accessing app preferences (language, etc.)
 * @param appDispatchers Dispatcher provider for coroutine execution
 */
class TvShowRepositoryImpl(
    private val tvShowsRemoteDataSource: TvShowsRemoteDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers
) : TvShowsRepository {

    private var popularTvShowsCurrentPage = 0
    private var popularTvShowsTotalPages = 0

    private var onTheAirTvShowsCurrentPage = 0
    private var onTheAirTvShowsTotalPages = 0

    private var topRatedTvShowsCurrentPage = 0
    private var topRatedTvShowsTotalPages = 0

    private val popularTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    private val onTheAirTvShows = MutableStateFlow<List<TvShow>>(emptyList())
    private val topRatedTvShows = MutableStateFlow<List<TvShow>>(emptyList())

    /**
     * Application-scoped coroutine scope that lives for the entire app lifetime.
     * Never cancelled as this repository is a singleton.
     */
    private val repositoryScope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    init {
        // Listen to language changes and refresh TV shows when language changes
        repositoryScope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on screen entry
                .collect {
                    popularTvShowsCurrentPage = 0
                    popularTvShowsTotalPages = 0
                    popularTvShows.value = emptyList()

                    onTheAirTvShowsCurrentPage = 0
                    onTheAirTvShowsTotalPages = 0
                    onTheAirTvShows.value = emptyList()

                    topRatedTvShowsCurrentPage = 0
                    topRatedTvShowsTotalPages = 0
                    topRatedTvShows.value = emptyList()

                    loadPopularTvShowsNextPage()
                    loadOnTheAirTvShowsNextPage()
                    loadTopRatedTvShowsNextPage()
                }
        }
    }

    /**
     * Observes popular TV shows from in-memory storage.
     * Returns a flow of TV shows from the in-memory cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observePopularTvShows(): Flow<List<TvShow>> {
        if (popularTvShows.value.isEmpty()) {
            loadPopularTvShowsNextPage()
        }

        return popularTvShows
    }

    /**
     * Loads the next page of TV shows from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadPopularTvShowsNextPage(): AppResult<Unit> {
        if (popularTvShowsTotalPages > 0 && popularTvShowsCurrentPage >= popularTvShowsTotalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = popularTvShowsCurrentPage + 1

        return when (val result =
            tvShowsRemoteDataSource.getPopularTvShowsPage(nextPage, getLanguage())) {
            is AppResult.Success -> {
                popularTvShowsTotalPages = result.data.totalPages
                val newTvShows = result.data.results.map { it.toDomain() }
                popularTvShows.value = popularTvShows.value + newTvShows
                popularTvShowsCurrentPage = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Observes on-the-air TV shows from in-memory storage.
     * Returns a flow of TV shows from the in-memory cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observeOnTheAirTvShows(): Flow<List<TvShow>> {
        if (onTheAirTvShows.value.isEmpty()) {
            loadOnTheAirTvShowsNextPage()
        }

        return onTheAirTvShows
    }

    /**
     * Loads the next page of on-the-air TV shows from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadOnTheAirTvShowsNextPage(): AppResult<Unit> {
        if (onTheAirTvShowsTotalPages > 0 && onTheAirTvShowsCurrentPage >= onTheAirTvShowsTotalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = onTheAirTvShowsCurrentPage + 1

        return when (val result =
            tvShowsRemoteDataSource.getOnTheAirTvShowsPage(nextPage, getLanguage())) {
            is AppResult.Success -> {
                onTheAirTvShowsTotalPages = result.data.totalPages
                val newTvShows = result.data.results.map { it.toDomain() }
                onTheAirTvShows.value = onTheAirTvShows.value + newTvShows
                onTheAirTvShowsCurrentPage = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Observes top-rated TV shows from in-memory storage.
     * Returns a flow of TV shows from the in-memory cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observeTopRatedTvShows(): Flow<List<TvShow>> {
        if (topRatedTvShows.value.isEmpty()) {
            loadTopRatedTvShowsNextPage()
        }

        return topRatedTvShows
    }

    /**
     * Loads the next page of top-rated TV shows from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadTopRatedTvShowsNextPage(): AppResult<Unit> {
        if (topRatedTvShowsTotalPages > 0 && topRatedTvShowsCurrentPage >= topRatedTvShowsTotalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = topRatedTvShowsCurrentPage + 1

        return when (val result =
            tvShowsRemoteDataSource.getTopRatedTvShowsPage(nextPage, getLanguage())) {
            is AppResult.Success -> {
                topRatedTvShowsTotalPages = result.data.totalPages
                val newTvShows = result.data.results.map { it.toDomain() }
                topRatedTvShows.value = topRatedTvShows.value + newTvShows
                topRatedTvShowsCurrentPage = nextPage

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Refreshes all TV show data by resetting pagination state and loading fresh data
     * for all three categories (popular, on-the-air, top-rated) in parallel.
     *
     * @return AppResult<List<TvShow>> Either:
     *   - AppResult.Success with the combined refreshed list of all TV shows if successful
     *   - AppResult.Error if any of the refresh operations failed
     */
    override suspend fun refresh(): AppResult<List<TvShow>> = coroutineScope {
        // Reset all three caches
        popularTvShowsCurrentPage = 0
        popularTvShowsTotalPages = 0
        popularTvShows.value = emptyList()

        onTheAirTvShowsCurrentPage = 0
        onTheAirTvShowsTotalPages = 0
        onTheAirTvShows.value = emptyList()

        topRatedTvShowsCurrentPage = 0
        topRatedTvShowsTotalPages = 0
        topRatedTvShows.value = emptyList()

        // Load all three categories in parallel
        val results = awaitAll(
            async { loadPopularTvShowsNextPage() },
            async { loadOnTheAirTvShowsNextPage() },
            async { loadTopRatedTvShowsNextPage() }
        )

        // Check if any failed
        val error = results.firstOrNull { it is AppResult.Error } as? AppResult.Error
        if (error != null) {
            return@coroutineScope error
        }

        // All succeeded - return combined list
        val combinedList = popularTvShows.value + onTheAirTvShows.value + topRatedTvShows.value
        return@coroutineScope AppResult.Success(combinedList)
    }

    override suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails = coroutineScope {
        val language = getLanguage()

        // Fetch details and videos in parallel
        val detailsDeferred = async { tvShowsRemoteDataSource.getTvShowDetails(tvShowId, language) }
        val videosDeferred = async { tvShowsRemoteDataSource.getTvShowVideos(tvShowId, language) }

        val detailsResult = detailsDeferred.await()
        val videosResult = videosDeferred.await()

        // Extract details or throw exception if failed
        val details = when (detailsResult) {
            is AppResult.Success -> detailsResult.data
            is AppResult.Error -> throw detailsResult.throwable ?: Exception(detailsResult.message)
        }

        // Map videos to domain and filter for trailers/teasers
        val trailers = when (videosResult) {
            is AppResult.Success -> {
                videosResult.data.results
                    .filter { it.type == "Trailer" || it.type == "Teaser" }
                    .sortedWith(compareByDescending<RemoteVideo> { it.official }
                        .thenByDescending { it.publishedAt })
                    .take(10)
                    .map { it.toDomain() }
            }
            is AppResult.Error -> emptyList()
        }

        // Combine details with trailers
        details.toDomain().copy(trailers = trailers)
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
