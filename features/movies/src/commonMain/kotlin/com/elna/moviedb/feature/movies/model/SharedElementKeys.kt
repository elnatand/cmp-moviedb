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

    /**
     * Key prefix for cast member profile image shared element transitions.
     * Append the person ID to create a unique key: `"${CAST_MEMBER}${personId}"`
     */
    const val CAST_MEMBER = "cast-member-"
}