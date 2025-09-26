package com.example.moviedb.core.data.model.movies

import com.example.moviedb.core.model.MovieDetails
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)

fun RemoteMovieDetails.toDomain(): MovieDetails = MovieDetails(
    id = id,
    title = title,
    overview = overview,
    poster_path = poster_path,
    backdrop_path = backdrop_path
)
