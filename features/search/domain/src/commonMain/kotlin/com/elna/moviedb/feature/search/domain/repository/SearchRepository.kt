package com.elna.moviedb.feature.search.domain.repository

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchPage

/**
 * Repository interface for search operations.
 * Uses the [SearchFilter] parameter to avoid per-category duplication.
 *
 * To add a new search category:
 * 1. Add a new value to the [SearchFilter] enum
 * 2. Register its execution strategy in the data layer (SearchFilterExecutor)
 * 3. No changes needed to this interface
 */
interface SearchRepository {
    /**
     * Searches for content based on the specified filter category.
     *
     * @param filter The search filter category (ALL, MOVIES, TV_SHOWS, PEOPLE)
     * @param query The search query string
     * @param page The page number to fetch
     * @return AppResult with the requested [SearchPage] (empty for a blank query)
     */
    suspend fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): AppResult<SearchPage>
}