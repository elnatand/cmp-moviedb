package com.elna.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elna.moviedb.core.database.model.MovieDetailsEntity
import com.elna.moviedb.core.database.model.VideoEntity

@Dao
interface MovieDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(item: MovieDetailsEntity)

    @Query("SELECT * FROM MovieDetailsEntity WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?

    @Query("DELETE FROM MovieDetailsEntity")
    suspend fun clearAllMovieDetails()

    @Query("SELECT * FROM videos WHERE movie_id = :movieId ORDER BY official DESC, published_at DESC")
    suspend fun getVideosForMovie(movieId: Int): List<VideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("DELETE FROM videos WHERE movie_id = :movieId")
    suspend fun deleteVideosForMovie(movieId: Int)
}