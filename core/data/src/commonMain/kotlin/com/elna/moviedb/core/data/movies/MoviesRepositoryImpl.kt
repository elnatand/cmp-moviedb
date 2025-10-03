package com.elna.moviedb.core.data.movies


import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.data.model.asEntity
import com.elna.moviedb.core.database.MoviesLocalDataSource
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.core.network.MoviesRemoteDataSource
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
 * Implementation of MoviesRepository that manages movie data from remote API and local cache.
 *
 * **Lifecycle:** This repository is an application-scoped singleton managed by Koin DI.
 * The [repositoryScope] is never cancelled and lives for the entire application lifetime.
 * This is intentional as the repository maintains app-wide state and language change observers.
 *
 * @param moviesRemoteDataSource Remote data source for fetching movies from API
 * @param moviesLocalDataSource Local data source for caching movies in database
 * @param preferencesManager Manager for accessing app preferences (language, etc.)
 * @param appDispatcher Dispatcher provider for coroutine execution
 */
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatcher: AppDispatcher
) : MoviesRepository {

    private var currentPage = 0
    private var totalPages = 0

    private val _errorState = MutableStateFlow<AppResult.Error?>(null)

    /**
     * Application-scoped coroutine scope that lives for the entire app lifetime.
     * Never cancelled as this repository is a singleton.
     */
    private val repositoryScope = CoroutineScope(SupervisorJob() + appDispatcher.getDispatcher())

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
     * Observes all movies from local storage with reactive error handling.
     *
     * This function returns a Flow that combines local movie data with error state,
     * automatically loading the first page if no data is cached locally.
     *
     * @return Flow<MDResponse<List<Movie>>> A reactive flow that emits:
     *   - AppResult.Success with list of movies when data is available and no errors
     *   - AppResult.Error when there are loading errors from loadNextPage()
     *
     * The flow automatically reacts to:
     * - Changes in local movie data
     * - Error state changes from network operations
     * - Initial data loading if cache is empty
     */
    override suspend fun observeAllMovies(): Flow<AppResult<List<Movie>>> {
        val localMoviesPageStream = moviesLocalDataSource.getAllMoviesAsFlow()

        // Load initial data if empty
        if (localMoviesPageStream.first().isEmpty()) {
            loadNextPage()
        }

        return combine(
            localMoviesPageStream,
            _errorState
        ) { movieEntities, error ->
            // Return error if present
            error?.let { return@combine it }

            // Return success with movie data
            AppResult.Success(
                data = movieEntities.map {
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
     * Loads the next page of movies from the remote API.
     *
     * This function handles pagination by:
     * 1. Clearing any previous error state
     * 2. Calculating the next page number based on current page
     * 3. Fetching data from remote API
     * 4. On success: updating total pages, caching data locally, and updating current page
     * 5. On error: storing error state in reactive _errorState for UI consumption
     *
     * The error state is automatically propagated to observeAllMovies() subscribers
     * through the reactive _errorState flow.
     *
     * Side effects:
     * - Updates currentPage and totalPages on successful load
     * - Caches new movie data in local storage
     * - Emits error state reactively if API call fails
     */
    override suspend fun loadNextPage() {

        if (totalPages > 0 && currentPage >= totalPages) {
            return  // All pages loaded
        }

        _errorState.value = null

        val nextPage = currentPage + 1

        when (val result = moviesRemoteDataSource.getPopularMoviesPage(nextPage, getLanguage())) {
            is AppResult.Success -> {
                totalPages = result.data.totalPages
                val entities = result.data.results.map { it.asEntity(nextPage) }
                moviesLocalDataSource.insertMoviesPage(entities)
                currentPage = nextPage
            }

            is AppResult.Error -> {
                _errorState.value = result
            }
        }
    }

    /**
     * Refreshes the movie data by resetting pagination state and loading fresh data.
     *
     * This function performs a complete refresh by:
     * 1. Resetting pagination state (currentPage = 0, totalPages = 0)
     * 2. Clearing any previous error state
     * 3. Loading the first page of movies via loadNextPage()
     * 4. Returning the result based on the loading outcome
     *
     * @return MDResponse<List<Movie>> Either:
     *   - MDResponse.Success with the refreshed list of movies if successful
     *   - MDResponse.Error if the refresh operation failed
     *
     * Note: This function immediately returns the result of the refresh operation.
     * For reactive updates, use observeAllMovies() which will automatically
     * reflect the refreshed state.
     */
    override suspend fun refresh(): AppResult<List<Movie>> {

        currentPage = 0
        totalPages = 0
        _errorState.value = null

        loadNextPage()

        // Return result based on loading outcome
        return _errorState.value ?: run {
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
        currentPage = 0
        totalPages = 0
        _errorState.value = null
        moviesLocalDataSource.clearAllMovies()
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
