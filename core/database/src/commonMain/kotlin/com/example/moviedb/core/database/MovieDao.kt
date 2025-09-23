package com.example.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert
    suspend fun insertMovie(item: MovieEntity)

    @Query("SELECT * FROM MovieEntity WHERE page = :page ORDER BY id")
    fun getMoviesByPageAsFlow(page: Int): Flow<List<MovieEntity>>

    @Query("SELECT * FROM MovieEntity ORDER BY timestamp")
    fun getAllMoviesAsFlow(): Flow<List<MovieEntity>>
}