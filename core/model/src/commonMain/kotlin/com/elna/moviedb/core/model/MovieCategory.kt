package com.elna.moviedb.core.model

/**
 * Represents different categories of movies.
 *
 * This is a pure domain model with no infrastructure dependencies.
 * Following Clean Architecture - keeps domain independent of API details.
 *
 * This enum enables the Open/Closed Principle by allowing new categories
 * to be added without modifying existing repository or ViewModel code.
 *
 * Mapping to infrastructure (e.g., TMDB API paths) is handled in the network layer.
 */
enum class MovieCategory {
    POPULAR,
    TOP_RATED,
    NOW_PLAYING
}
