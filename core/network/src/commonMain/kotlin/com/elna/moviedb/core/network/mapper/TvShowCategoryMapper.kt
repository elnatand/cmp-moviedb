package com.elna.moviedb.core.network.mapper

import com.elna.moviedb.core.model.TvShowCategory

/**
 * Maps domain TvShowCategory to TMDB API endpoint paths.
 *
 * Following Clean Architecture - keeps domain layer independent of infrastructure.
 * This mapper lives in the network layer where TMDB-specific details belong.
 *
 * Following Open/Closed Principle - adding new categories requires only adding a new case here.
 *
 * @return TMDB API TV show category endpoint path (e.g., "tv/popular", "tv/on_the_air")
 */
fun TvShowCategory.toTmdbPath(): String = when (this) {
    TvShowCategory.POPULAR -> "tv/popular"
    TvShowCategory.ON_THE_AIR -> "tv/on_the_air"
    TvShowCategory.TOP_RATED -> "tv/top_rated"
}
