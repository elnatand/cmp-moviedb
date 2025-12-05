package com.elna.moviedb.feature.movies.model

/**
 * Shared element transition keys for movie-related screens.
 */
object SharedElementKeys {
    /**
     * Key prefix for movie poster shared element transitions.
     * Append the movie ID to create a unique key: `"${MOVIE_POSTER}${movieId}"`
     */
    const val MOVIE_POSTER = "movie-poster-"
}