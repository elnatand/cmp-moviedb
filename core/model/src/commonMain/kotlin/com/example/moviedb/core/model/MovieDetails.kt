package com.example.moviedb.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)