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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
 * @param appDispatchers Dispatcher provider for coroutine execution
 */
class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource,
    private val preferencesManager: PreferencesManager,
    private val appDispatchers: AppDispatchers
) : MoviesRepository {

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
     * Observes all movies from local storage.
     * Returns a flow of movies from the local cache.
     * Automatically triggers initial load if cache is empty.
     */
    override suspend fun observeAllMovies(): Flow<List<Movie>> {
        val localMoviesPageStream = moviesLocalDataSource.getAllMoviesAsFlow()

        // Load initial data if empty (non-blocking)
        repositoryScope.launch {
            if (localMoviesPageStream.first().isEmpty()) {
                loadNextPage()
            }
        }

        return localMoviesPageStream.map { movieEntities ->
            movieEntities.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    poster_path = it.poster_path
                )
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
     * Loads the next page of movies from the remote API.
     *
     * @return AppResult<Unit> Success if page loaded, Error if loading failed
     */
    override suspend fun loadNextPage(): AppResult<Unit> {
        val currentLanguage = getLanguage()
        val paginationState = preferencesManager.getMoviesPaginationState().first()

        if (paginationState.totalPages > 0 && paginationState.currentPage >= paginationState.totalPages) {
            return AppResult.Success(Unit)  // All pages loaded
        }

        val nextPage = paginationState.currentPage + 1

        return when (val result = moviesRemoteDataSource.getPopularMoviesPage(nextPage, currentLanguage)) {
            is AppResult.Success -> {
                val newTotalPages = result.data.totalPages
                val entities = result.data.results.map { it.asEntity() }
                moviesLocalDataSource.insertMoviesPage(entities)

                // Save pagination state to DataStore
                preferencesManager.saveMoviesPaginationState(
                    PaginationState(
                        currentPage = nextPage,
                        totalPages = newTotalPages
                    )
                )

                AppResult.Success(Unit)
            }

            is AppResult.Error -> result
        }
    }

    /**
     * Refreshes the movie data by resetting pagination state and loading fresh data.
     *
     * @return AppResult<List<Movie>> Either:
     *   - AppResult.Success with the refreshed list of movies if successful
     *   - AppResult.Error if the refresh operation failed
     */
    override suspend fun refresh(): AppResult<List<Movie>> {
        // Reset pagination state in DataStore
        preferencesManager.saveMoviesPaginationState(
            PaginationState(
                currentPage = 0,
                totalPages = 0
            )
        )

        return when (val result = loadNextPage()) {
            is AppResult.Success -> {
                // Get the current data from local storage
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
            is AppResult.Error -> result
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
            )
        )
        moviesLocalDataSource.clearAllMovies()
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}
