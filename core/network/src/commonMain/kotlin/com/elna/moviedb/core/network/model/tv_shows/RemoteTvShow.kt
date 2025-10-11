package com.elna.moviedb.core.network.model.tv_shows

import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShow(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("poster_path")
    val posterPath: String?,
)

fun RemoteTvShow.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        posterPath = posterPath?.let { "$TMDB_IMAGE_URL$it" }
    )
}
