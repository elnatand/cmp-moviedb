package com.example.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviedb.core.database.model.MovieDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(item: MovieDetailsEntity)

    @Query("SELECT * FROM MovieDetailsEntity WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?
}