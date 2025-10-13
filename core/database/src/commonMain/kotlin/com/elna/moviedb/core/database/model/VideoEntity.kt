package com.elna.moviedb.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.elna.moviedb.core.model.Video
import com.elna.moviedb.core.model.VideoSite

@Entity(
    tableName = "videos",
    foreignKeys = [
        ForeignKey(
            entity = MovieDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("movie_id")]
)
data class VideoEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "movie_id") val movieId: Int,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean,
    @ColumnInfo(name = "published_at") val publishedAt: String?
) {
    fun toDomain(): Video = Video(
        id = id,
        key = key,
        name = name,
        site = VideoSite.fromString(site),
        type = type,
        official = official
    )
}

fun Video.asEntity(movieId: Int, publishedAt: String? = null): VideoEntity = VideoEntity(
    id = id,
    movieId = movieId,
    key = key,
    name = name,
    site = site.name,
    type = type,
    official = official,
    publishedAt = publishedAt
)
