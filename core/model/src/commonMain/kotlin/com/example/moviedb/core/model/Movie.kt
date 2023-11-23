package com.example.moviedb.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
)