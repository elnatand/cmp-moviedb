package com.example.moviedb.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TvShowDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)