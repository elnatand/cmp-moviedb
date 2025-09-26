package com.example.moviedb.core.data.model.movies

import com.example.moviedb.core.model.MovieDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class RemoteMovieDetails(
    @SerialName("")
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
    val genres: List<Genre>?,
    val production_companies: List<ProductionCompany>?,
    val production_countries: List<ProductionCountry>?,
    val spoken_languages: List<SpokenLanguage>?
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class ProductionCompany(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

@Serializable
data class ProductionCountry(
    val iso_3166_1: String,
    val name: String
)

@Serializable
data class SpokenLanguage(
    val english_name: String,
    val iso_639_1: String,
    val name: String
)

fun RemoteMovieDetails.toDomain(): MovieDetails = MovieDetails(
    id = id,
    title = title,
    overview = overview,
    poster_path = poster_path,
    backdrop_path = backdrop_path,
    release_date = release_date,
    runtime = runtime,
    vote_average = vote_average,
    vote_count = vote_count,
    adult = adult,
    budget = budget,
    revenue = revenue,
    homepage = homepage,
    imdb_id = imdb_id,
    original_language = original_language,
    original_title = original_title,
    popularity = popularity,
    status = status,
    tagline = tagline,
    genres = genres?.map { it.name },
    production_companies = production_companies?.map { it.name },
    production_countries = production_countries?.map { it.name },
    spoken_languages = spoken_languages?.map { it.english_name }
)
