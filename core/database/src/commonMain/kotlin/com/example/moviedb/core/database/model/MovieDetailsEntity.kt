package com.example.moviedb.core.database.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)