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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
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

    private var currentPage = 0
    private var totalPages = 0

    private val _tvShows = MutableStateFlow<List<TvShow>>(emptyList())
    private val _initialLoadError = MutableStateFlow<AppResult.Error?>(null)

    /**
     * SharedFlow for pagination errors with extraBufferCapacity = 1.
     *
     * extraBufferCapacity = 1 ensures that if an error is emitted before the UI subscriber
     * is ready to collect, the error won't be lost. Without this, emit() could suspend
     * indefinitely if there are no active collectors, potentially blocking the repository.
     */
    private val _paginationError = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)

    /**
     * Exposes pagination errors as a SharedFlow for UI consumption.
     * Emits error messages when loading additional pages fails.
     */
    override val paginationErrors: SharedFlow<String> = _paginationError.asSharedFlow()

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
                    _initialLoadError.value = null
                    _tvShows.value = emptyList()
                    loadNextPage()
                }
        }
    }

    /**
     * Observes all TV shows from in-memory storage with reactive error handling.
     *
     * This function returns a Flow that combines in-memory TV show data with error state.
     * Unlike the Movies repository which uses Room database for persistence, this uses
     * in-memory storage that is cleared when the app is closed.
     *
     * Behavior:
     * 1. In-memory data is shown when available
     * 2. Initial load errors are shown only when memory is empty
     * 3. Pagination errors are emitted separately via paginationErrors flow
     * 4. The flow does NOT emit when memory is empty and no error (initial loading state)
     *
     * @return Flow<AppResult<List<TvShow>>> A reactive flow that emits:
     *   - AppResult.Success with in-memory TV shows when data is available
     *   - AppResult.Error only when initial load fails and memory is empty
     *   - Nothing when memory is empty and loading (ViewModel keeps LOADING state)
     *
     * Note: Automatically triggers initial data loading if memory is empty.
     */
    override suspend fun observeAllTvShows(): Flow<AppResult<List<TvShow>>> {
        // Load initial data if empty
        if (_tvShows.value.isEmpty()) {
            loadNextPage()
        }

        return combine(
            _tvShows,
            _initialLoadError
        ) { tvShows, error ->
            when {
                // Always show in-memory data if available (even with pagination errors)
                tvShows.isNotEmpty() -> AppResult.Success(tvShows)

                // Show error only if memory is empty (initial load failed)
                error != null -> error

                // Memory is empty and loading - return null to filter out this emission
                else -> null
            }
        }.filterNotNull() // Only emit when we have data or error, not during initial loading
    }

    /**
     * Loads the next page of TV shows from the remote API with proper error handling.
     *
     * This function handles pagination by:
     * 1. Determining if this is an initial load or pagination
     * 2. Fetching data from remote API
     * 3. On success: updating total pages, appending data to memory, clearing error states
     * 4. On error:
     *    - Initial load (page 0): Sets _initialLoadError to block UI with error screen
     *    - Pagination: Emits error via _paginationError for snackbar display
     *
     * Error handling strategy:
     * - Initial load errors show error screen when memory is empty
     * - Pagination errors are non-blocking (show snackbar while keeping in-memory data)
     *
     * Side effects:
     * - Updates currentPage and totalPages on successful load
     * - Stores new TV show data in memory (not persisted to disk)
     * - Emits appropriate error based on context (initial vs pagination)
     */
    override suspend fun loadNextPage() {

        if (totalPages > 0 && currentPage >= totalPages) {
            return  // All pages loaded
        }

        val nextPage = currentPage + 1
        val isInitialLoad = currentPage == 0

        when (val result = tvShowsRemoteDataSource.getPopularTvShowsPage(nextPage, getLanguage())) {
            is AppResult.Success -> {
                totalPages = result.data.totalPages
                val newTvShows = result.data.results.map { it.toDomain() }
                _tvShows.value = _tvShows.value + newTvShows
                currentPage = nextPage

                // Clear initial error on successful load
                if (isInitialLoad) {
                    _initialLoadError.value = null
                }
            }

            is AppResult.Error -> {
                if (isInitialLoad) {
                    // Initial load failed - block UI with error screen
                    _initialLoadError.value = result
                } else {
                    // Pagination failed - emit non-blocking error for snackbar
                    _paginationError.emit(result.message)
                }
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
     *
     * @return AppResult<List<TvShow>> Either:
     *   - AppResult.Success with the refreshed list of TV shows if successful
     *   - AppResult.Error if the refresh operation failed
     *
     * Note: This function immediately returns the result of the refresh operation.
     * For reactive updates, use observeAllTvShows() which will automatically
     * reflect the refreshed state.
     */
    override suspend fun refresh(): AppResult<List<TvShow>> {
        currentPage = 0
        totalPages = 0
        _initialLoadError.value = null
        _tvShows.value = emptyList()

        loadNextPage()

        // Return result based on loading outcome
        return _initialLoadError.value ?: AppResult.Success(_tvShows.value)
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
