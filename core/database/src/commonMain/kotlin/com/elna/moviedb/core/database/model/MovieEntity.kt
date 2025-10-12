package com.elna.moviedb.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MovieCategory {
    POPULAR,
    TOP_RATED,
    NOW_PLAYING
}

@Entity
data class MovieEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val title: String,
    val poster_path: String?,
    val category: String = MovieCategory.POPULAR.name
)