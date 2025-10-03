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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Implementation of TvShowsRepository that manages TV show data from remote API.
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

    private var currentPage = 0
    private var totalPages = 0

    private val _tvShows = MutableStateFlow<List<TvShow>>(emptyList())
    private val _errorState = MutableStateFlow<AppResult.Error?>(null)

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
                    currentPage = 0
                    totalPages = 0
                    _errorState.value = null
                    _tvShows.value = emptyList()
                    loadNextPage()
                }
        }
    }

    /**
     * Observes all TV shows from memory with reactive error handling.
     *
     * This function returns a Flow that combines in-memory TV show data with error state,
     * automatically loading the first page if no data is cached in memory.
     *
     * @return Flow<AppResult<List<TvShow>>> A reactive flow that emits:
     *   - AppResult.Success with list of TV shows when data is available and no errors
     *   - AppResult.Error when there are loading errors from loadNextPage()
     */
    override suspend fun observeAllTvShows(): Flow<AppResult<List<TvShow>>> {
        // Load initial data if empty
        if (_tvShows.value.isEmpty()) {
            loadNextPage()
        }

        return combine(
            _tvShows,
            _errorState
        ) { tvShows, error ->
            // Return error if present
            error?.let { return@combine it }

            // Return success with TV show data
            AppResult.Success(tvShows)
        }
    }

    /**
     * Loads the next page of TV shows from the remote API.
     *
     * This function handles pagination by:
     * 1. Clearing any previous error state
     * 2. Calculating the next page number based on current page
     * 3. Fetching data from remote API
     * 4. On success: updating total pages, appending data to memory list, and updating current page
     * 5. On error: storing error state in reactive _errorState for UI consumption
     */
    override suspend fun loadNextPage() {

        if (totalPages > 0 && currentPage >= totalPages) {
            return  // All pages loaded
        }

        _errorState.value = null

        val nextPage = currentPage + 1

        when (val result = tvShowsRemoteDataSource.getPopularTvShowsPage(nextPage, getLanguage())) {
            is AppResult.Success -> {
                totalPages = result.data.totalPages
                val newTvShows = result.data.results.map { it.toDomain() }
                _tvShows.value = _tvShows.value + newTvShows
                currentPage = nextPage
            }

            is AppResult.Error -> {
                _errorState.value = result
            }
        }
    }

    /**
     * Refreshes the TV show data by resetting pagination state and loading fresh data.
     *
     * This function performs a complete refresh by:
     * 1. Resetting pagination state (currentPage = 0, totalPages = 0)
     * 2. Clearing any previous error state and in-memory data
     * 3. Loading the first page of TV shows via loadNextPage()
     * 4. Returning the result based on the loading outcome
     */
    override suspend fun refresh(): AppResult<List<TvShow>> {
        currentPage = 0
        totalPages = 0
        _errorState.value = null
        _tvShows.value = emptyList()

        loadNextPage()

        // Return result based on loading outcome
        return _errorState.value ?: AppResult.Success(_tvShows.value)
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
