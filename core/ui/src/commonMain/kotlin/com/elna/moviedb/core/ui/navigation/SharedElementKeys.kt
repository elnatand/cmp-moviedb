package com.elna.moviedb.core.ui.navigation

/**
 * Shared element transition keys used across the app.
 * These keys are used to match elements between screens for shared element transitions.
 */
object SharedElementKeys {
    /**
     * Key prefix for movie poster shared element transitions.
     * Append the movie ID and category to create a unique key: `"${MOVIE_POSTER}${movieId}-${category}"`
     */
    const val MOVIE_POSTER = "movie-poster-"

    /**
     * Key prefix for TV show poster shared element transitions.
     * Append the TV show ID and category to create a unique key: `"${TV_SHOW_POSTER}${tvShowId}-${category}"`
     */
    const val TV_SHOW_POSTER = "tv-show-poster-"
}
