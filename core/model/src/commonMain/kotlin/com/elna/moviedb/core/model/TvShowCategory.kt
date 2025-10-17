package com.elna.moviedb.core.model

/**
 * Represents different categories of TV shows available from TMDB API.
 *
 * This enum enables the Open/Closed Principle by allowing new categories
 * to be added without modifying existing repository or ViewModel code.
 *
 * @property apiPath The TMDB API endpoint path for this category
 */
enum class TvShowCategory(val apiPath: String) {
    POPULAR("tv/popular"),
    ON_THE_AIR("tv/on_the_air"),
    TOP_RATED("tv/top_rated")
}
