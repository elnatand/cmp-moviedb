package com.example.moviedb.core.database.model


data class MovieEntity(
    val id: Int,
    val title: String,
    val poster_path: String?,
)