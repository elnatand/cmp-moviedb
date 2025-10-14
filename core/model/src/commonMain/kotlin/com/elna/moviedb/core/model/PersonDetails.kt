package com.elna.moviedb.core.model

data class PersonDetails(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val deathday: String?,
    val gender: String,
    val homepage: String?,
    val imdbId: String?,
    val knownForDepartment: String,
    val placeOfBirth: String?,
    val popularity: Double?,
    val profilePath: String?,
    val adult: Boolean,
    val alsoKnownAs: List<String>,
    val filmography: List<FilmographyCredit> = emptyList()
)