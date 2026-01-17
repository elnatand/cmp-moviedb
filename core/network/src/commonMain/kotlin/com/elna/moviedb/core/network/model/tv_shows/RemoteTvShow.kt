package com.elna.moviedb.core.network.model.tv_shows


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
