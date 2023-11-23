package com.example.moviedb.core.model

import kotlinx.serialization.Serializable

@Serializable
data class TvShow(
    val id: Int,
    val name: String,
    val poster_path: String?,
)