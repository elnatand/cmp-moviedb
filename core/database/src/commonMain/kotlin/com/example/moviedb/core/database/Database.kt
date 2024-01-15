package com.example.moviedb.core.database

import DatabaseDriverFactory
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class Database(databaseDriverFactory: DatabaseDriverFactory) {

    private val database = MovieDbDatabase(databaseDriverFactory.createDriver())

    fun getAllPlayers(): Flow<List<HockeyPlayer>> =
        database.hockeyPlayerQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
}
