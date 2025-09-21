package com.example.moviedb.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)