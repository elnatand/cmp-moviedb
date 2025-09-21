package com.example.moviedb.core.data.movies

import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun observeMoviesPage(page: Int): Flow<List<Movie>>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}