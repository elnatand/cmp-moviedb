package com.example.moviedb.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieDetailsEntity(
    @PrimaryKey val id: Int,
    val page: Int,
    val title: String,
    val poster_path: String?,
)