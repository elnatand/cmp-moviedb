package com.elna.moviedb.core.model

/**
 * Represents different categories of movies available from TMDB API.
 *
 * This enum enables the Open/Closed Principle by allowing new categories
 * to be added without modifying existing repository or ViewModel code.
 *
 * @property apiPath The TMDB API endpoint path for this category
 */
enum class MovieCategory(val apiPath: String) {
    POPULAR("movie/popular"),
    TOP_RATED("movie/top_rated"),
    NOW_PLAYING("movie/now_playing")
}
