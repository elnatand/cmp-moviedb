package com.elna.moviedb.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.elna.moviedb.core.model.Review

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["movie_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MovieDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = true
        )
    ]
)
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "movie_id")
    val movieId: Int,
    val author: String,
    @ColumnInfo(name = "avatar_path")
    val avatarPath: String?,
    val rating: Double?,
    val content: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String
) {
    fun toDomain() = Review(
        id = id,
        author = author,
        avatarPath = avatarPath,
        rating = rating,
        content = content,
        createdAt = createdAt
    )
}
