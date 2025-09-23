package com.example.moviedb.core.data.model.movies

import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovie(
    val id: Int,
    val title: String,
    val poster_path: String?,
)

fun NetworkMovie.asEntity(page: Int) = MovieEntity(
    id = id,
    page = page,
    title = title,
    poster_path = poster_path
)