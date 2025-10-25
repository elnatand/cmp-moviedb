package com.elna.moviedb.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.elna.moviedb.core.database.model.CastMemberEntity
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

    @Query("DELETE FROM videos")
    suspend fun clearAllVideos()

    @Query("SELECT * FROM cast_members WHERE movie_id = :movieId ORDER BY `order` ASC")
    suspend fun getCastForMovie(movieId: Int): List<CastMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastMembers(cast: List<CastMemberEntity>)

    @Query("DELETE FROM cast_members WHERE movie_id = :movieId")
    suspend fun deleteCastForMovie(movieId: Int)

    @Query("DELETE FROM cast_members")
    suspend fun clearAllCast()

    @Transaction
    suspend fun replaceCastForMovie(movieId: Int, cast: List<CastMemberEntity>) {
        deleteCastForMovie(movieId)
        if (cast.isNotEmpty()) insertCastMembers(cast)
    }
}