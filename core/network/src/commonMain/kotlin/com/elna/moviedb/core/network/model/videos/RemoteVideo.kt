package com.elna.moviedb.core.network.model.videos

import com.elna.moviedb.core.model.Video
import com.elna.moviedb.core.model.VideoSite
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteVideo(
    @SerialName("id")
    val id: String,
    @SerialName("iso_639_1")
    val language: String? = null,
    @SerialName("iso_3166_1")
    val country: String? = null,
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("site")
    val site: String,
    @SerialName("size")
    val size: Int? = null,
    @SerialName("type")
    val type: String,
    @SerialName("official")
    val official: Boolean,
    @SerialName("published_at")
    val publishedAt: String? = null
)

fun RemoteVideo.toDomain() = Video(
    id = id,
    key = key,
    name = name,
    site = VideoSite.fromString(site),
    type = type,
    official = official
)
