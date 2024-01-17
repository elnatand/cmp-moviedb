package com.example.moviedb.core.data.movies

import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun observeMoviesPage(page: Int): Flow<List<Movie>> {
        val localMoviesPageStream = moviesLocalDataSource.getMoviesPage(page)
        if (localMoviesPageStream.first().isEmpty()) {
            val remoteMoviesPage = moviesRemoteDataSource.getMoviesPage(page)
            moviesLocalDataSource.insertMoviesPage(movies = remoteMoviesPage, page = page)
        }

        return localMoviesPageStream
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return moviesRemoteDataSource.getMovieDetails(movieId)
    }
}