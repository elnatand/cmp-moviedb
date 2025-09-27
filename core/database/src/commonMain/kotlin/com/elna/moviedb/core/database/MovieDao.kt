package com.elna.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elna.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(item: MovieEntity)

    @Query("SELECT * FROM MovieEntity WHERE page = :page ORDER BY timestamp")
    fun getMoviesByPageAsFlow(page: Int): Flow<List<MovieEntity>>

    @Query("SELECT * FROM MovieEntity ORDER BY timestamp")
    fun getAllMoviesAsFlow(): Flow<List<MovieEntity>>
}