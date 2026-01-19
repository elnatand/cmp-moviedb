package com.elna.moviedb.feature.movies.mappers

import com.elna.moviedb.feature.movies.model.MovieCategory

/**
 * Maps domain MovieCategory to TMDB API endpoint paths.
 *
 * Following Clean Architecture - keeps domain layer independent of infrastructure.
 * This mapper lives in the network layer where TMDB-specific details belong.
 *
 * @return TMDB API movie category endpoint path (e.g., "movie/popular", "movie/top_rated")
 */
fun MovieCategory.toTmdbPath(): String = when (this) {
    MovieCategory.POPULAR -> "/movie/popular"
    MovieCategory.TOP_RATED -> "/movie/top_rated"
    MovieCategory.NOW_PLAYING -> "/movie/now_playing"
}
