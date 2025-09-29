package com.elna.moviedb.core.model


data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String?,
    val runtime: Int?,
    val vote_average: Double?,
    val vote_count: Int?,
    val adult: Boolean?,
    val budget: Long?,
    val revenue: Long?,
    val homepage: String?,
    val imdb_id: String?,
    val original_language: String?,
    val original_title: String?,
    val popularity: Double?,
    val status: String?,
    val tagline: String?,
    val genres: List<String>?,
    val production_companies: List<String>?,
    val production_countries: List<String>?,
    val spoken_languages: List<String>?
)
