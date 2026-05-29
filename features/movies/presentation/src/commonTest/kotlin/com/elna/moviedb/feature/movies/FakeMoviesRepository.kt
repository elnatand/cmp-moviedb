package com.elna.moviedb.feature.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.Movie
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.MovieDetails
import com.elna.moviedb.feature.movies.repositories.MoviesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake implementation of MoviesRepository for testing.
 * Provides controllable behavior for all repository operations.
 */
class FakeMoviesRepository : MoviesRepository {
    private val moviesFlows = mutableMapOf<MovieCategory, MutableStateFlow<List<Movie>>>()
    private val nextPageResults = mutableMapOf<MovieCategory, AppResult<Unit>>()

    val loadNextPageCallCount = mutableMapOf<MovieCategory, Int>()
    var clearAndReloadCallCount = 0
    var clearAndReloadDelay = 0L
    var clearAndReloadResult: AppResult<Unit> = AppResult.Success(Unit)

    /**
     * Optional suspension applied inside [loadMoviesNextPage] after the call is counted.
     * Use a non-zero value to keep a load "in flight" so tests can verify that the
     * ViewModel's in-progress guard prevents duplicate concurrent loads.
     */
    var loadNextPageDelay = 0L

    /** Result returned by [getMovieDetails]; configurable per detail-screen test. */
    var detailsResult: AppResult<MovieDetails> = AppResult.Error("Not configured")

    /** Optional suspension applied inside [getMovieDetails] to keep a fetch "in flight". */
    var detailsDelay = 0L

    init {
        // Initialize flows and counters for all categories
        MovieCategory.entries.forEach { category ->
            moviesFlows[category] = MutableStateFlow(emptyList())
            loadNextPageCallCount[category] = 0
        }
    }

    fun setMoviesForCategory(category: MovieCategory, movies: List<Movie>) {
        moviesFlows[category]?.value = movies
    }

    fun setNextPageResult(category: MovieCategory, result: AppResult<Unit>) {
        nextPageResults[category] = result
    }

    /**
     * Resets all interaction counters. Useful after constructing the ViewModel under test,
     * which triggers one initial load per empty category, so individual tests can assert
     * only the calls produced by their own actions.
     */
    fun resetCounters() {
        MovieCategory.entries.forEach { loadNextPageCallCount[it] = 0 }
        clearAndReloadCallCount = 0
    }

    override fun observeMovies(category: MovieCategory): Flow<List<Movie>> {
        return moviesFlows[category] ?: flowOf(emptyList())
    }

    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
        loadNextPageCallCount[category] = (loadNextPageCallCount[category] ?: 0) + 1
        if (loadNextPageDelay > 0) {
            delay(loadNextPageDelay)
        }
        return nextPageResults[category] ?: AppResult.Success(Unit)
    }

    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        if (detailsDelay > 0) {
            delay(detailsDelay)
        }
        return detailsResult
    }

    override suspend fun clearAndReload(): AppResult<Unit> {
        clearAndReloadCallCount++
        if (clearAndReloadDelay > 0) {
            delay(clearAndReloadDelay)
        }
        return clearAndReloadResult
    }
}
