package com.example.moviedb.core.data.movies

import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails

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