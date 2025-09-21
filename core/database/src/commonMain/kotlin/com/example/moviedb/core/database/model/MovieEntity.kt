package com.example.moviedb.core.database.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    val id: Int,
    val title: String,
    val poster_path: String?,
)