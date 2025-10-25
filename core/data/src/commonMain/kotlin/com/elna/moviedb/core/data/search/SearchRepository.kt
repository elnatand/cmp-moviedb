package com.elna.moviedb.core.data.search

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for search operations.
 * Uses category parameter to avoid duplication.
 *
 * To add a new search category:
 * 1. Add new value to [SearchFilter] enum
 * 2. Update [SearchRepositoryImpl] to handle the new category
 * 3. No changes needed to this interface
 */
interface SearchRepository {
    /**
     * Searches for content based on the specified filter category.
     *
     * @param filter The search filter category (ALL, MOVIES, TV_SHOWS, PEOPLE)
     * @param query The search query string
     * @param page The page number to fetch
     * @return Flow emitting AppResult with list of search results
     */
    fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>>
}