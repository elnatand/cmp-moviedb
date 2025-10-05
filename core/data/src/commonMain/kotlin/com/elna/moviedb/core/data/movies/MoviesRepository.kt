package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow


interface MoviesRepository {

    /**
     * Flow that emits pagination error messages.
     * Used to display non-blocking errors (snackbars) when loading additional pages fails.
     */
    val paginationErrors: SharedFlow<String>

    suspend fun observeAllMovies(): Flow<AppResult<List<Movie>>>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun loadNextPage()
    suspend fun refresh(): AppResult<List<Movie>>
    suspend fun clearMovies()
}