package com.elna.moviedb.feature.tvshows.domain.model

/**
 * Domain model representing a TV show.
 *
 * @property id Unique identifier for the TV show from TMDB
 * @property name TV show title/name
 * @property posterPath Full URL to the poster image (includes base URL from TMDB_IMAGE_URL)
 */
data class TvShow(
    val id: Int,
    val name: String,
    val posterPath: String?,
)
