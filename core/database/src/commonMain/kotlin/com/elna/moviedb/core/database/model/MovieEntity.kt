package com.elna.moviedb.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MovieEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val title: String,
    val posterPath: String?,
    val category: String = ""
)
