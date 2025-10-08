package com.elna.moviedb.core.datastore.model

/**
 * Represents pagination state for a paginated data source.
 *
 * @property currentPage The current page number (0-indexed means no pages loaded yet)
 * @property totalPages The total number of pages available from the API
 * @property lastUpdated Timestamp of the last successful page load (in milliseconds)
 * @property language The language code used when fetching data (e.g., "en-US")
 */
data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val lastUpdated: Long = 0L,
    val language: String = ""
)
