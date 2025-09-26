package com.example.moviedb.core.data.movies

import com.example.moviedb.core.model.MDResponse
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {

    suspend fun observeAllMovies(): Flow<MDResponse<List<Movie>>>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun loadNextPage()
    suspend fun refresh(): MDResponse<List<Movie>>
}