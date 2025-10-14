package com.elna.moviedb.core.model

data class FilmographyCredit(
    val id: Int,
    val title: String?,
    val name: String?,
    val character: String?,
    val posterPath: String?,
    val releaseDate: String?,
    val firstAirDate: String?,
    val mediaType: MediaType,
    val voteAverage: Double?
) {
    val displayTitle: String
        get() = title ?: name ?: ""

    val displayYear: String?
        get() = releaseDate?.take(4) ?: firstAirDate?.take(4)
}

enum class MediaType {
    MOVIE, TV
}
