package com.example.moviedb.core.data.model.movies

import kotlinx.serialization.Serializable

@Serializable
data class RemoteMoviesPage(
    val page: Int,
    val total_pages: Int,
    val results: List<RemoteMovie>
)