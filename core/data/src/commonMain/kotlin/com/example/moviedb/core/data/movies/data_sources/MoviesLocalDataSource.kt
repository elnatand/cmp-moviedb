package com.example.moviedb.core.data.movies.data_sources


import com.example.moviedb.core.database.MovieDao
import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

class MoviesLocalDataSource(
    private val movieDao: MovieDao
) {
    fun getMoviesPage(page: Int): Flow<List<MovieEntity>> {
        return movieDao.getMoviesByPageAsFlow(page)
    }
    fun getAllMovies(): Flow<List<MovieEntity>> {
        return movieDao.getAllMoviesAsFlow()
    }


   suspend fun insertMoviesPage(movies: List<MovieEntity>) {
        movies.forEach {
            movieDao.insertMovie(it)
        }
    }
}
