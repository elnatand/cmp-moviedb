package com.elna.moviedb.feature.search.data.mapper

import com.elna.moviedb.feature.search.domain.model.SearchFilter

/**
 * Maps domain SearchFilter to TMDB API endpoint paths.
 *
 * Following Clean Architecture - keeps domain layer independent of infrastructure.
 * This mapper lives in the network layer where TMDB-specific details belong.
 *
 * @return TMDB API search endpoint path (e.g., "search/multi", "search/movie")
 */
fun SearchFilter.toTmdbPath(): String = when (this) {
    SearchFilter.ALL -> "/search/multi"
    SearchFilter.MOVIES -> "/search/movie"
    SearchFilter.TV_SHOWS -> "/search/tv"
    SearchFilter.PEOPLE -> "/search/person"
}
