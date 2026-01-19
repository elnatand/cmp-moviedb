package com.elna.moviedb.feature.person.mappar

import com.elna.moviedb.core.network.model.person.RemoteCastCredit
import com.elna.moviedb.core.network.model.person.RemoteCombinedCredits
import com.elna.moviedb.core.network.model.person.RemoteCrewCredit
import com.elna.moviedb.core.network.model.person.RemotePersonDetails
import com.elna.moviedb.feature.person.model.FilmographyCredit
import com.elna.moviedb.feature.person.model.MediaType
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

fun RemoteCombinedCredits.toDomain(): List<FilmographyCredit> {
    val castCredits = cast?.map { it.toDomain() } ?: emptyList()
    val crewCredits = crew?.map { it.toDomain() } ?: emptyList()

    return (castCredits + crewCredits)
        .distinctBy { it.id }
        .sortedByDescending { credit ->
            credit.releaseDate ?: credit.firstAirDate ?: ""
        }
}

fun RemoteCastCredit.toDomain(): FilmographyCredit {
    return FilmographyCredit(
        id = id ?: 0,
        title = title,
        name = name,
        character = character,
        posterPath = posterPath,
        releaseDate = releaseDate,
        firstAirDate = firstAirDate,
        mediaType = when (mediaType) {
            "tv" -> MediaType.TV
            else -> MediaType.MOVIE
        },
        voteAverage = voteAverage
    )
}

fun RemoteCrewCredit.toDomain(): FilmographyCredit {
    return FilmographyCredit(
        id = id ?: 0,
        title = title,
        name = name,
        character = job, // Use job as the "character" field for crew
        posterPath = posterPath,
        releaseDate = releaseDate,
        firstAirDate = firstAirDate,
        mediaType = when (mediaType) {
            "tv" -> MediaType.TV
            else -> MediaType.MOVIE
        },
        voteAverage = voteAverage
    )
}