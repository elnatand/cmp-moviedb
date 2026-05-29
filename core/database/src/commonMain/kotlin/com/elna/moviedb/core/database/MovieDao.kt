package com.elna.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elna.moviedb.core.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM MovieEntity WHERE category = :category ORDER BY position")
    fun getMoviesByCategoryAsFlow(category: String): Flow<List<MovieEntity>>

    @Query("DELETE FROM MovieEntity")
    suspend fun clearAllMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)
}