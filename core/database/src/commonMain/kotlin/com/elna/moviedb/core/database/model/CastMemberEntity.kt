package com.elna.moviedb.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.elna.moviedb.core.model.CastMember

@Entity(
    tableName = "cast_members", indices = [
        Index(value = ["movie_id"]),
        Index(value = ["person_id"]),
        Index(value = ["movie_id", "person_id"], unique = true)
    ], foreignKeys = [
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
data class CastMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val dbId: Int = 0,
    @ColumnInfo(name = "movie_id")
    val movieId: Int,
    @ColumnInfo(name = "person_id")
    val personId: Int,
    val name: String,
    val character: String,
    @ColumnInfo(name = "profile_path")
    val profilePath: String?,
    val order: Int
) {
    fun toDomain() = CastMember(
        id = personId,
        name = name,
        character = character,
        profilePath = profilePath,
        order = order
    )
}
