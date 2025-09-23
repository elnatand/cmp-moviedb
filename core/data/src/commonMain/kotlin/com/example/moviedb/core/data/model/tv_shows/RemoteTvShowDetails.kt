package com.example.moviedb.core.data.model.tv_shows


import com.example.moviedb.core.model.TvShowDetails
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShowDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: String,
    val backdrop_path: String?
)

fun RemoteTvShowDetails.toDomain() = TvShowDetails(
    id = id,
    name = name,
    overview = overview,
    poster_path = poster_path,
    backdrop_path = backdrop_path
)
