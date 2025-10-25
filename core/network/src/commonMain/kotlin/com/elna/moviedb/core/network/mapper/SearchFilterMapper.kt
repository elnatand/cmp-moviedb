package com.elna.moviedb.core.network.mapper

import com.elna.moviedb.core.model.SearchFilter

/**
 * Maps domain SearchFilter to TMDB API endpoint paths.
 *
 * Following Clean Architecture - keeps domain layer independent of infrastructure.
 * This mapper lives in the network layer where TMDB-specific details belong.
 *
 * Following Open/Closed Principle - adding new filters requires only adding a new case here.
 *
 * @return TMDB API search endpoint path (e.g., "search/multi", "search/movie")
 */
fun SearchFilter.toTmdbPath(): String = when (this) {
    SearchFilter.ALL -> "/search/multi"
    SearchFilter.MOVIES -> "/search/movie"
    SearchFilter.TV_SHOWS -> "/search/tv"
    SearchFilter.PEOPLE -> "/search/person"
}
