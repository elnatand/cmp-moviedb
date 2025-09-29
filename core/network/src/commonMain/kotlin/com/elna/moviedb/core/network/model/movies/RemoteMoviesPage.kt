package com.elna.moviedb.core.network.model.movies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMoviesPage(
    @SerialName("page")
    val page: Int,
    @SerialName("total_pages")
    val totaPages: Int,
    @SerialName("results")
    val results: List<RemoteMovie>
)