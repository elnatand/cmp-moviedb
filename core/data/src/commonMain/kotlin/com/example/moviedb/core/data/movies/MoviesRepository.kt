package com.example.moviedb.core.data.movies

import com.example.moviedb.core.data.model.NetworkMovieDetails
import com.example.moviedb.core.model.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun observeMoviesPage(page: Int): Flow<List<Movie>>
    suspend fun getMovieDetails(movieId: Int): NetworkMovieDetails
}