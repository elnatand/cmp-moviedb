package com.elna.moviedb.core.datastore.model

/**
 * Represents pagination state for a paginated data source.
 *
 * @property currentPage The current page number (0-indexed means no pages loaded yet)
 * @property totalPages The total number of pages available from the API
 */
data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0
)
