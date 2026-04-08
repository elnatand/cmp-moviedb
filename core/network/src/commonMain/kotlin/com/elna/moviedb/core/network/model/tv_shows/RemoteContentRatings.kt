package com.elna.moviedb.core.network.model.tv_shows

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteContentRatingsResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("results")
    val results: List<RemoteContentRating>
)

@Serializable
data class RemoteContentRating(
    @SerialName("iso_3166_1")
    val iso31661: String,
    @SerialName("rating")
    val rating: String,
    @SerialName("descriptors")
    val descriptors: List<String> = emptyList()
)
