package com.elna.moviedb.feature.search.domain.model

/**
 * A single page of search results plus the pagination metadata needed to decide
 * whether another page is worth requesting.
 */
data class SearchPage(
    val items: List<SearchResultItem>,
    val page: Int,
    val totalPages: Int,
) {
    val hasMorePages: Boolean get() = page < totalPages
}
