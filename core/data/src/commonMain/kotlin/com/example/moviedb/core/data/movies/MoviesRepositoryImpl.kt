package com.example.moviedb.core.data.movies

import com.example.moviedb.core.data.model.movies.asEntity
import com.example.moviedb.core.data.movies.data_sources.MoviesLocalDataSource
import com.example.moviedb.core.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.core.data.model.movies.toDomain
import com.example.moviedb.core.data.model.movies.toEntity
import com.example.moviedb.core.database.model.MovieDetailsEntity
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class MoviesRepositoryImpl(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesLocalDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun observeMoviesPage(page: Int): Flow<List<Movie>> {
        val localMoviesPageStream = moviesLocalDataSource.getMoviesPage(page)
        if (localMoviesPageStream.first().isEmpty()) {
            loadPage(page)
        }

        return localMoviesPageStream.map { list ->
            list.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    poster_path = it.poster_path
                )
            }
        }
    }

    override suspend fun observeAllMovies(initialPage: Int): Flow<List<Movie>> {
        val localMoviesPageStream = moviesLocalDataSource.getAllMovies()
        if (localMoviesPageStream.first().isEmpty()) {
            loadPage(initialPage)
        }

        return localMoviesPageStream.map { list ->
            list.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    poster_path = it.poster_path
                )
            }
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        val localMovieDetails = moviesLocalDataSource.getMoviesDetails(movieId)
        if (localMovieDetails == null) {
            val remoteMovieDetails = moviesRemoteDataSource.getMovieDetails(movieId)
            moviesLocalDataSource.insertMovieDetails(remoteMovieDetails.toEntity())
        }
        return moviesLocalDataSource.getMoviesDetails(movieId)!!.toDomain()
    }

    override suspend fun loadPage(page: Int) {
        val remoteMoviesPage = moviesRemoteDataSource.getMoviesPage(page)
        moviesLocalDataSource.insertMoviesPage(
            movies = remoteMoviesPage.map { it.asEntity(page) },
        )
    }
}
