package com.example.moviedb.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkMoviesPage(
    val page: Int,
    val total_pages: Int,
    val results: List<NetworkMovie>
)