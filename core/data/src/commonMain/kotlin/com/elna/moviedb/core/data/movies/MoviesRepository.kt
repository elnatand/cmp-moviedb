package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow


interface MoviesRepository {

    suspend fun observeAllMovies(): Flow<List<Movie>>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun loadNextPage(): AppResult<Unit>
    suspend fun refresh(): AppResult<List<Movie>>
    suspend fun clearMovies()
}