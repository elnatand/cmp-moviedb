package com.elna.moviedb.feature.person.mappar

import com.elna.moviedb.core.network.model.person.RemotePersonDetails
import com.elna.moviedb.feature.person.model.PersonDetails

fun RemotePersonDetails.toDomain(): PersonDetails {
    return PersonDetails(
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