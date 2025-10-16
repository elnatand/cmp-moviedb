package com.elna.moviedb.core.network.model.person

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemotePersonDetails(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("biography")
    val biography: String? = null,
    @SerialName("birthday")
    val birthday: String? = null,
    @SerialName("deathday")
    val deathday: String? = null,
    @SerialName("gender")
    val gender: Int? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("known_for_department")
    val knownForDepartment: String? = null,
    @SerialName("place_of_birth")
    val placeOfBirth: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("profile_path")
    val profilePath: String? = null,
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("also_known_as")
    val alsoKnownAs: List<String>? = null
)

fun RemotePersonDetails.toDomain(): com.elna.moviedb.core.model.PersonDetails {
    return com.elna.moviedb.core.model.PersonDetails(
        id = id ?: 0,
        name = name ?: "",
        biography = biography ?: "",
        birthday = birthday,
        deathday = deathday,
        gender = when (gender) {
            1 -> "Female"
            2 -> "Male"
            3 -> "Non-binary"
            else -> "Not specified"
        },
        homepage = homepage,
        imdbId = imdbId,
        knownForDepartment = knownForDepartment ?: "",
        placeOfBirth = placeOfBirth,
        popularity = popularity,
        profilePath = profilePath,
        adult = adult ?: false,
        alsoKnownAs = alsoKnownAs ?: emptyList()
    )
}