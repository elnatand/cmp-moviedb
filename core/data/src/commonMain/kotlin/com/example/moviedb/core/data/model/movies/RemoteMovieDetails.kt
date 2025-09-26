package com.example.moviedb.core.data.model.movies

import com.example.moviedb.core.model.MovieDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class RemoteMovieDetails(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("overview")
    val overview: String,
    @SerialName("poster_path")
    val poster_path: String?,
    @SerialName("backdrop_path")
    val backdrop_path: String?,
    @SerialName("release_date")
    val release_date: String?,
    @SerialName("runtime")
    val runtime: Int?,
    @SerialName("vote_average")
    val vote_average: Double?,
    @SerialName("vote_count")
    val vote_count: Int?,
    @SerialName("adult")
    val adult: Boolean?,
    @SerialName("budget")
    val budget: Long?,
    @SerialName("revenue")
    val revenue: Long?,
    @SerialName("homepage")
    val homepage: String?,
    @SerialName("imdb_id")
    val imdb_id: String?,
    @SerialName("original_language")
    val original_language: String?,
    @SerialName("original_title")
    val original_title: String?,
    @SerialName("popularity")
    val popularity: Double?,
    @SerialName("status")
    val status: String?,
    @SerialName("tagline")
    val tagline: String?,
    @SerialName("genres")
    val genres: List<Genre>?,
    @SerialName("production_companies")
    val production_companies: List<ProductionCompany>?,
    @SerialName("production_countries")
    val production_countries: List<ProductionCountry>?,
    @SerialName("spoken_languages")
    val spoken_languages: List<SpokenLanguage>?
)

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Serializable
data class ProductionCompany(
    @SerialName("id")
    val id: Int,
    @SerialName("logo_path")
    val logo_path: String?,
    @SerialName("name")
    val name: String,
    @SerialName("origin_country")
    val origin_country: String
)

@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso_3166_1: String,
    @SerialName("name")
    val name: String
)

@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val english_name: String,
    @SerialName("iso_639_1")
    val iso_639_1: String,
    @SerialName("name")
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
