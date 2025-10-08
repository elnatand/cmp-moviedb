package com.elna.moviedb.core.data.movies


import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.data.model.asEntity
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.datastore.model.PaginationState
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.core.network.MoviesRemoteDataSource
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Implementation of MoviesRepository that manages movie data from remote API and local cache.
 *
 * **Lifecycle:** This repository is an application-scoped singleton managed by Koin DI.
 * The [repositoryScope] is never cancelled and lives for the entire application lifetime.
 * This is intentional as the repository maintains app-wide state and language change observers.
 *
 * @param moviesRemoteDataSource Remote data source for fetching movies from API
 * @param moviesLocalDataSource Local data source for caching movies in database
 * @param preferencesManager Manager for accessing app preferences (language, etc.)
 * @param appDispatchers Dispatcher provider for coroutine execution
 */
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers
) : MoviesRepository {

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
        // Listen to language changes and clear movies when language changes
        repositoryScope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on screen entry
                .collect {
                    clearMovies()
                    loadNextPage()
                }
        }
    }


    /**
     * Observes all movies from local storage with offline-first architecture.
     *
     * This function implements an offline-first approach where:
     * 1. Local database is the source of truth
     * 2. Remote errors only shown when cache is empty (initial load)
     * 3. Pagination errors are emitted separately via paginationErrors flow
     *
     * @return Flow<AppResult<List<Movie>>> A reactive flow that emits:
     *   - AppResult.Success with cached movies (even during pagination errors)
     *   - AppResult.Error only when initial load fails and cache is empty
     *
     * The flow automatically reacts to:
     * - Changes in local movie data
     * - Initial load error state
     * - Triggers initial data loading if cache is empty
     */
    override suspend fun observeAllMovies(): Flow<AppResult<List<Movie>>> {
        val localMoviesPageStream = moviesLocalDataSource.getAllMoviesAsFlow()

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadNextPage()
        }

        return combine(
            localMoviesPageStream,
            _initialLoadError
        ) { movieEntities, error ->
            when {
                // Offline-first: Always show local data if available
                movieEntities.isNotEmpty() -> AppResult.Success(
                    data = movieEntities.map {
                        Movie(
                            id = it.id,
                            title = it.title,
                            poster_path = it.poster_path
                        )
                    }
                )

                // Only show error if cache is empty (initial load failed)
                error != null -> error

                // Empty success state
                else -> AppResult.Success(emptyList())
            }
        }
    }


    /**
     * Retrieves detailed information for a specific movie.
     *
     * This function implements a cache-first strategy:
     * 1. First checks local storage for cached movie details
     * 2. If not found locally, fetches from remote API
     * 3. Caches the remote result locally for future use
     * 4. Returns the movie details converted to domain model
     *
     * @param movieId The unique identifier of the movie to retrieve
     * @return MovieDetails The complete movie details including cast, crew, and metadata
     * @throws Exception If the remote API call fails and no local data is available
     */
    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        val localMovieDetails = moviesLocalDataSource.getMoviesDetails(movieId)
        if (localMovieDetails == null) {
            when (val result = moviesRemoteDataSource.getMovieDetails(movieId, getLanguage())) {
                is AppResult.Success -> {
                    moviesLocalDataSource.insertMovieDetails(result.data.asEntity())
                }

                is AppResult.Error -> {
                    throw Exception(result.message)
                }
            }
        }
        return moviesLocalDataSource.getMoviesDetails(movieId)!!.toDomain()
    }

    /**
     * Loads the next page of movies from the remote API with offline-first error handling.
     *
     * This function handles pagination by:
     * 1. Retrieving pagination state from DataStore (survives app restarts)
     * 2. Validating language consistency (resets if language changed)
     * 3. Determining if this is an initial load or pagination
     * 4. Fetching data from remote API
     * 5. On success: updating pagination state in DataStore, caching data locally, clearing error states
     * 6. On error:
     *    - Initial load (page 0): Sets _initialLoadError to block UI
     *    - Pagination: Emits error via _paginationError for snackbar display
     *
     * Error handling strategy:
     * - Initial load errors prevent UI from showing (show error screen)
     * - Pagination errors are non-blocking (show snackbar while keeping cached data)
     *
     * Side effects:
     * - Updates pagination state in DataStore on successful load
     * - Caches new movie data in local storage
     * - Emits appropriate error based on context (initial vs pagination)
     */
    override suspend fun loadNextPage() {
        val currentLanguage = getLanguage()
        val paginationState = preferencesManager.getMoviesPaginationState().first()

        // Reset pagination if language changed
        val currentPage = if (paginationState.language != currentLanguage) 0 else paginationState.currentPage
        val totalPages = if (paginationState.language != currentLanguage) 0 else paginationState.totalPages

        if (totalPages > 0 && currentPage >= totalPages) {
            return  // All pages loaded
        }

        val nextPage = currentPage + 1
        val isInitialLoad = currentPage == 0

        when (val result = moviesRemoteDataSource.getPopularMoviesPage(nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map { it.asEntity() }
                moviesLocalDataSource.insertMoviesPage(entities)

                // Save pagination state to DataStore
                @OptIn(ExperimentalTime::class)
                preferencesManager.saveMoviesPaginationState(
                    PaginationState(
                        currentPage = nextPage,
                        totalPages = newTotalPages,
                        lastUpdated = Clock.System.now().epochSeconds,
                        language = currentLanguage
                    )
                )

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
     * Refreshes the movie data by resetting pagination state and loading fresh data.
     *
     * This function performs a complete refresh by:
     * 1. Resetting pagination state in DataStore (currentPage = 0, totalPages = 0)
     * 2. Clearing any previous error states
     * 3. Loading the first page of movies via loadNextPage()
     * 4. Returning the result based on the loading outcome
     *
     * @return AppResult<List<Movie>> Either:
     *   - AppResult.Success with the refreshed list of movies if successful
     *   - AppResult.Error if the refresh operation failed
     *
     * Note: This function immediately returns the result of the refresh operation.
     * For reactive updates, use observeAllMovies() which will automatically
     * reflect the refreshed state.
     */
    override suspend fun refresh(): AppResult<List<Movie>> {
        // Reset pagination state in DataStore
        preferencesManager.saveMoviesPaginationState(
            com.elna.moviedb.core.datastore.model.PaginationState(
                currentPage = 0,
                totalPages = 0,
                lastUpdated = 0L,
                language = ""
            )
        )
        _initialLoadError.value = null

        loadNextPage()

        // Return result based on loading outcome
        return _initialLoadError.value ?: run {
            // If no error, get the current data from local storage
            val localMovies = moviesLocalDataSource.getAllMoviesAsFlow().first()
            AppResult.Success(
                data = localMovies.map {
                    Movie(
                        id = it.id,
                        title = it.title,
                        poster_path = it.poster_path
                    )
                }
            )
        }
    }

    /**
     * Clears all cached movies from local storage and resets pagination state.
     *
     * This function should be called when language changes to ensure movies
     * are re-fetched in the new language.
     */
    override suspend fun clearMovies() {
        // Reset pagination state in DataStore
        preferencesManager.saveMoviesPaginationState(
            PaginationState(
                currentPage = 0,
                totalPages = 0,
                lastUpdated = 0L,
                language = ""
            )
        )
        _initialLoadError.value = null
        moviesLocalDataSource.clearAllMovies()
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
