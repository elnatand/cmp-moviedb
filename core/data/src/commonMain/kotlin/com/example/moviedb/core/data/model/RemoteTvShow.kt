package com.example.moviedb.core.data.model

import com.example.moviedb.core.model.TvShow
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTvShow(
    val id: Int,
    val name: String,
    val poster_path: String?,
)

fun NetworkTvShow.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        poster_path = poster_path
    )
}
