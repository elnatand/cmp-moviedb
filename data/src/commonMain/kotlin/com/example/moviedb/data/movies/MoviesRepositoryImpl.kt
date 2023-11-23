package com.example.moviedb.data.movies

import com.example.moviedb.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.model.Movie
import com.example.moviedb.model.MovieDetails

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource
) : MoviesRepository {

    override suspend fun getMoviesPage(page:Int): List<Movie> {
        return moviesRemoteDataSource.getMoviesPage(page)
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return moviesRemoteDataSource.getMovieDetails(movieId)
    }
}