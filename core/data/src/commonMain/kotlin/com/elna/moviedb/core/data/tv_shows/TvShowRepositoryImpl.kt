package com.elna.moviedb.core.data.tv_shows

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.core.network.TvShowsRemoteDataSource
import com.elna.moviedb.core.network.model.tv_shows.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
     * Observes all TV shows from in-memory storage.
     * Returns a flow of TV shows from the in-memory cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observePopularTvShows(): Flow<List<TvShow>> {
        // Load initial data if empty (non-blocking)
        repositoryScope.launch {
            if (popularTvShows.value.isEmpty()) {
                loadPopularTvShowsNextPage()
            }
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

        return when (val result = tvShowsRemoteDataSource.getPopularTvShowsPage(nextPage, getLanguage())) {
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
        // Load initial data if empty (non-blocking)
        repositoryScope.launch {
            if (onTheAirTvShows.value.isEmpty()) {
                loadOnTheAirTvShowsNextPage()
            }
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

        return when (val result = tvShowsRemoteDataSource.getOnTheAirTvShowsPage(nextPage, getLanguage())) {
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
        // Load initial data if empty (non-blocking)
        repositoryScope.launch {
            if (topRatedTvShows.value.isEmpty()) {
                loadTopRatedTvShowsNextPage()
            }
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

        return when (val result = tvShowsRemoteDataSource.getTopRatedTvShowsPage(nextPage, getLanguage())) {
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
     * Refreshes the TV show data by resetting pagination state and loading fresh data.
     *
     * @return AppResult<List<TvShow>> Either:
     *   - AppResult.Success with the refreshed list of TV shows if successful
     *   - AppResult.Error if the refresh operation failed
     */
    override suspend fun refresh(): AppResult<List<TvShow>> {
        popularTvShowsCurrentPage = 0
        popularTvShowsTotalPages = 0
        popularTvShows.value = emptyList()

        return when (val result = loadPopularTvShowsNextPage()) {
            is AppResult.Success -> AppResult.Success(popularTvShows.value)
            is AppResult.Error -> result
        }
    }

    override suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails {
        return tvShowsRemoteDataSource.getTvShowDetails(tvShowId, getLanguage()).toDomain()
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
