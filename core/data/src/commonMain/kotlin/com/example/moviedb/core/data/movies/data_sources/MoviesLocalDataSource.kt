package com.example.moviedb.core.data.movies.data_sources


import com.example.moviedb.core.database.MovieDao
import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

class MoviesLocalDataSource(
    private val movieDao: MovieDao
) {
    fun getMoviesPage(page: Int): Flow<List<MovieEntity>> {
        return movieDao.getAllAsFlow()
    }

   suspend fun insertMoviesPage(movies: List<MovieEntity>, page: Int) {
        movies.forEach {
            movieDao.insert(it)
        }
    }
}
