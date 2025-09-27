package com.example.moviedb.core.data.model.tv_shows

import com.example.moviedb.core.model.TvShow
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShow(
    val id: Int,
    val name: String,
    val poster_path: String?,
)

fun RemoteTvShow.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        poster_path = poster_path
    )
}
