package com.elna.moviedb.core.model

/**
 * Represents different categories of TV shows.
 *
 * This is a pure domain model with no infrastructure dependencies.
 * Following Clean Architecture - keeps domain independent of API details.
 *
 * New categories can be added without modifying existing repository or ViewModel code.
 *
 * Mapping to infrastructure (e.g., TMDB API paths) is handled in the network layer.
 */
enum class TvShowCategory {
    POPULAR,
    ON_THE_AIR,
    TOP_RATED
}
