package com.example.moviedb.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moviedb.core.model.MovieDetails

@Entity
data class MovieDetailsEntity(
    @PrimaryKey val id: Int,
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
    val genres: String?,
    val production_companies: String?,
    val production_countries: String?,
    val spoken_languages: String?
) {
    fun toDomain(): MovieDetails = MovieDetails(
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
        genres = genres?.split(",")?.filter { it.isNotBlank() },
        production_companies = production_companies?.split(",")?.filter { it.isNotBlank() },
        production_countries = production_countries?.split(",")?.filter { it.isNotBlank() },
        spoken_languages = spoken_languages?.split(",")?.filter { it.isNotBlank() }
    )
}