package com.elna.moviedb.core.network.model.person

import com.elna.moviedb.core.model.FilmographyCredit
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
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
        posterPath = posterPath?.let { "$TMDB_IMAGE_URL$it" },
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
        posterPath = posterPath?.let { "$TMDB_IMAGE_URL$it" },
        releaseDate = releaseDate,
        firstAirDate = firstAirDate,
        mediaType = when (mediaType) {
            "tv" -> MediaType.TV
            else -> MediaType.MOVIE
        },
        voteAverage = voteAverage
    )
}
