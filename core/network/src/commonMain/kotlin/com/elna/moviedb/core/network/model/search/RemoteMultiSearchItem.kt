package com.elna.moviedb.core.network.model.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMultiSearchItem(
    @SerialName("id")
    val id: Int,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double?=null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
)
