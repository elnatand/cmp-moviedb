package com.example.moviedb.data.movies

import com.example.moviedb.model.Movie
import com.example.moviedb.model.MovieDetails

interface MoviesRepository {
    suspend fun getMoviesPage(page: Int): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
}