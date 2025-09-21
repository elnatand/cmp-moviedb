package com.example.moviedb.core.database

import DatabaseDriverFactory
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Database(databaseDriverFactory: DatabaseDriverFactory) {

    private val database = MovieDbDatabase(databaseDriverFactory.createDriver())

    fun getMoviesPage(page: Int): Flow<List<MovieEntity>> = database.moviesQueries
        .selectMovies(page.toLong())
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { list ->
            list.map {
                MovieEntity(
                    id = it.id.toInt(), title = it.title, poster_path = it.poster_path
                )
            }
        }

    fun insertMovie(movie: MovieEntity, page: Int) {
        database.moviesQueries.insert(
            id = movie.id.toLong(),
            title = movie.title,
            poster_path = movie.poster_path,
            page = page.toLong()
        )
    }
}
