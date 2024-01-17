package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.database.Database
import com.example.moviedb.core.model.Movie

import kotlinx.coroutines.flow.Flow

class MoviesLocalDataSource(
    private val database: Database
) {
    fun getMoviesPage(page: Int): Flow<List<Movie>> {
        return database.getMoviesPage(page)
    }

    fun insertMoviesPage(movies: List<Movie>, page: Int) {
        movies.forEach {
            database.insertMovie(movie = it, page = page)
        }
    }
}
