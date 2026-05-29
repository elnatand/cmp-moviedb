package com.elna.moviedb.feature.movies.model

/**
 * Represents different categories of movies.
 *
 * This is a pure domain model with no infrastructure dependencies.
 * Following Clean Architecture - keeps domain independent of API details.
 *
 * New categories can be added without modifying existing repository or ViewModel code.
 *
 * Mapping to infrastructure (e.g., TMDB API paths) is handled in the network layer.
 */
enum class MovieCategory {
    POPULAR,
    TOP_RATED,
    NOW_PLAYING
}
