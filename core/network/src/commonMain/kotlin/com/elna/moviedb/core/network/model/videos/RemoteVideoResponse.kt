package com.elna.moviedb.core.network.model.videos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteVideoResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("results")
    val results: List<RemoteVideo>
)
