package com.example.moviedb.core.data.movies

import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails

interface MoviesRepository {
    suspend fun getMoviesPage(page: Int): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}