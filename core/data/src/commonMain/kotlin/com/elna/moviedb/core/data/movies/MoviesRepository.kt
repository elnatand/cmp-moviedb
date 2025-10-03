package com.elna.moviedb.core.data.movies

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow


interface MoviesRepository {

    suspend fun observeAllMovies(): Flow<AppResult<List<Movie>>>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun loadNextPage()
    suspend fun refresh(): AppResult<List<Movie>>
    suspend fun clearMovies()
}