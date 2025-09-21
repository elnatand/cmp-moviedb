package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.database.Database
import com.example.moviedb.core.database.model.MovieEntity

import kotlinx.coroutines.flow.Flow

class MoviesLocalDataSource(
    private val database: Database
) {
    fun getMoviesPage(page: Int): Flow<List<MovieEntity>> {
        return database.getMoviesPage(page)
    }

    fun insertMoviesPage(movies: List<MovieEntity>, page: Int) {
        movies.forEach {
            database.insertMovie(movie = it, page = page)
        }
    }
}
