package com.elna.moviedb.feature.person.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCombinedCredits(
    @SerialName("cast")
    val cast: List<RemoteCastCredit>? = null,
    @SerialName("crew")
    val crew: List<RemoteCrewCredit>? = null,
    @SerialName("id")
    val id: Int? = null
)

@Serializable
data class RemoteCastCredit(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("character")
    val character: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null
)

@Serializable
data class RemoteCrewCredit(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("job")
    val job: String? = null,
    @SerialName("department")
    val department: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null
)

