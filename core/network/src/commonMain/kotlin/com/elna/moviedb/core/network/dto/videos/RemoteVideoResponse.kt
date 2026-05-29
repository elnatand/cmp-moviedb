package com.elna.moviedb.core.network.dto.videos

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.Video
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteVideoResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("results")
    val results: List<RemoteVideo>
)

fun RemoteVideoResponse.toTrailers(): List<Video> =
    results
        .filter { it.type == "Trailer" || it.type == "Teaser" }
        .sortedWith(
            compareByDescending<RemoteVideo> { it.official }
                .thenByDescending { it.publishedAt }
        )
        .map { it.toDomain() }

fun AppResult<RemoteVideoResponse>.toTrailersOrEmpty(): List<Video> = when (this) {
    is AppResult.Success -> data.toTrailers()
    is AppResult.Error -> emptyList()
}
