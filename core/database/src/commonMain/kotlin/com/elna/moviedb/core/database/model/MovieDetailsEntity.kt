package com.elna.moviedb.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieDetailsEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,
    val runtime: Int?,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @ColumnInfo(name = "vote_count") val
    voteCount: Int?,
    val adult: Boolean?,
    val budget: Long?,
    val revenue: Long?,
    val homepage: String?,
    @ColumnInfo(name = "imdb_id")
    val imdbId: String?,
    @ColumnInfo(name = "original_language")
    val originalLanguage: String?,
    @ColumnInfo(name = "original_title")
    val originalTitle: String?,
    val popularity: Double?,
    val status: String?,
    val tagline: String?,
    val genres: String?,
    @ColumnInfo(name = "production_companies")
    val productionCompanies: String?,
    @ColumnInfo(name = "production_countries")
    val productionCountries: String?,
    @ColumnInfo(name = "spoken_languages")
    val spokenLanguages: String?
)
