package com.example.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert
    suspend fun insert(item: MovieEntity)

    @Query("SELECT count(*) FROM MovieEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM MovieEntity")
    fun getAllAsFlow(): Flow<List<MovieEntity>>
}