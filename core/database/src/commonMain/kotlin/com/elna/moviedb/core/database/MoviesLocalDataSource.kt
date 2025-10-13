package com.elna.moviedb.core.database

import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.database.model.VideoEntity
import kotlinx.coroutines.flow.Flow

class MoviesLocalDataSource(
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao,
) {
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>> {
        return movieDao.getMoviesByCategoryAsFlow(category)
    }

    fun getAllMoviesAsFlow(): Flow<List<MovieEntity>> {
        return movieDao.getAllMoviesAsFlow()
    }

    suspend fun insertMoviesPage(movies: List<MovieEntity>) {
        movies.forEach {
            movieDao.insertMovie(it)
        }
    }

    suspend fun getMoviesDetails(movieId: Int): MovieDetailsEntity? {
        return movieDetailsDao.getMovieDetails(movieId)
    }

    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity) {
        movieDetailsDao.insertMovieDetails(movieDetails)
    }

    suspend fun clearMoviesByCategory(category: String) {
        movieDao.clearMoviesByCategory(category)
    }

    suspend fun clearAllMovies() {
        movieDao.clearAllMovies()
        movieDetailsDao.clearAllMovieDetails()
    }

    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity> {
        return movieDetailsDao.getVideosForMovie(movieId)
    }

    suspend fun insertVideos(videos: List<VideoEntity>) {
        movieDetailsDao.insertVideos(videos)
    }

    suspend fun deleteVideosForMovie(movieId: Int) {
        movieDetailsDao.deleteVideosForMovie(movieId)
    }
}