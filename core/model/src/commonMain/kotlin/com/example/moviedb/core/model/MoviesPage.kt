package com.example.moviedb.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MoviesPage(
    val page: Int,
    val total_pages: Int,
    val results: List<Movie>
)