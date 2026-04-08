package com.elna.moviedb.core.network.model.movies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteReleaseDatesResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("results")
    val results: List<RemoteReleaseDatesByCountry>
)

@Serializable
data class RemoteReleaseDatesByCountry(
    @SerialName("iso_3166_1")
    val iso31661: String,
    @SerialName("release_dates")
    val releaseDates: List<RemoteReleaseDate>
)

@Serializable
data class RemoteReleaseDate(
    @SerialName("certification")
    val certification: String,
    @SerialName("type")
    val type: Int,
    @SerialName("descriptors")
    val descriptors: List<String> = emptyList()
)
