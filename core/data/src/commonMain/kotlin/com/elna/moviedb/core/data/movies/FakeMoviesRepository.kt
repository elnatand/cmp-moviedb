package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.model.MovieDetails
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

    override suspend fun observeMovies(category: MovieCategory): Flow<List<Movie>> {
        return moviesFlows[category] ?: flowOf(emptyList())
    }

    override suspend fun loadMoviesNextPage(category: MovieCategory): AppResult<Unit> {
        loadNextPageCallCount[category] = (loadNextPageCallCount[category] ?: 0) + 1
        return nextPageResults[category] ?: AppResult.Success(Unit)
    }

    override suspend fun getMovieDetails(movieId: Int): AppResult<MovieDetails> {
        return AppResult.Error("Not implemented in fake")
    }

    override suspend fun clearAndReload() {
        clearAndReloadCallCount++
        if (clearAndReloadDelay > 0) {
            kotlinx.coroutines.delay(clearAndReloadDelay)
        }
    }
}
