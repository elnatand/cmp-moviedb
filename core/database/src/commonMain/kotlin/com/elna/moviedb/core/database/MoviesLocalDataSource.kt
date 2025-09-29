package com.elna.moviedb.core.database

import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MoviesLocalDataSource(
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao,
    private val appDispatcher: AppDispatcher
) {
    fun getAllMoviesAsFlow(): Flow<List<MovieEntity>> {
        return movieDao.getAllMoviesAsFlow()
    }


    suspend fun insertMoviesPage(movies: List<MovieEntity>) {
        movies.forEach {
            movieDao.insertMovie(it)
        }
    }

    suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity? {
        return withContext(appDispatcher.getDispatcher()) {
            movieDetailsDao.getMovieDetails(movieId)
        }
    }

    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity) {
        movieDetailsDao.insertMovieDetails(movieDetails)
    }
}