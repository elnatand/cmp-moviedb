package com.example.moviedb.core.data.movies

import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.first

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getMoviesPage(page:Int): List<Movie> {
        val x = moviesLocalDataSource.getAllPlayers().first()
        println("aaa= ${x.first().full_name}")
        return moviesRemoteDataSource.getMoviesPage(page)
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return moviesRemoteDataSource.getMovieDetails(movieId)
    }
}