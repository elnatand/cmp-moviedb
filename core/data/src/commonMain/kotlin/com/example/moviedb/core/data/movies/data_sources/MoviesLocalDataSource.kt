package com.example.moviedb.core.data.movies.data_sources

import com.example.moviedb.core.database.Database
import com.example.moviedb.core.database.HockeyPlayer
import kotlinx.coroutines.flow.Flow

class MoviesLocalDataSource(
    private val database: Database
) {
   fun getAllPlayers(): Flow<List<HockeyPlayer>> {
      return database.getAllPlayers()
   }
}
