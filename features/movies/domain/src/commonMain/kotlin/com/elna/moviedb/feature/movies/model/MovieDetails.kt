package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.model.CastMember
import com.elna.moviedb.core.model.Video

/**
 * Full movie details.
 *
 * @property trailers List of official trailer videos (e.g., YouTube). May be null when unavailable.
 *                    Contains only Trailer-type videos, not teasers/clips.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val adult: Boolean?,
    val budget: Long?,
    val revenue: Long?,
    val homepage: String?,
    val imdbId: String?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val popularity: Double?,
    val status: String?,
    val tagline: String?,
    val genres: List<String>?,
    val productionCompanies: List<String>?,
    val productionCountries: List<String>?,
    val spokenLanguages: List<String>?,
    val trailers: List<Video>? = null,
    val cast: List<CastMember>? = null
)
