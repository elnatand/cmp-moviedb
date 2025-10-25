package com.elna.moviedb.core.datastore

import com.elna.moviedb.core.datastore.model.PaginationState
import kotlinx.coroutines.flow.Flow

/**
 * Generic interface for managing pagination state for any category.
 *
 * This interface separates pagination concerns from app settings,
 * and uses generic category keys to support any content category without code changes.
 *
 * @see PreferencesManager for app settings (language, theme, etc.)
 */
interface PaginationPreferences {
    /**
     * Observes pagination state for a given category.
     *
     * @param category The category key (e.g., "POPULAR", "TOP_RATED")
     * @return Flow emitting pagination state for the category
     */
    fun getPaginationState(category: String): Flow<PaginationState>

    /**
     * Saves pagination state for a given category.
     *
     * @param category The category key (e.g., "POPULAR", "TOP_RATED")
     * @param state The pagination state to save
     */
    suspend fun savePaginationState(category: String, state: PaginationState)

    /**
     * Clears all pagination state for all categories.
     */
    suspend fun clearAllPaginationState()
}
