package com.elna.moviedb.core.datastore.pagination

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.elna.moviedb.core.datastore.pagination.PaginationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of PaginationPreferences using DataStore.
 *
 * This implementation uses dynamic preference keys based on category names,
 * enabling support for any category without code changes.
 *
 * Key format: `{category}_current_page` and `{category}_total_pages`
 *
 * @property dataStore The underlying DataStore instance
 */
internal class PaginationPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : PaginationPreferences {

    override fun getPaginationState(category: String): Flow<PaginationState> {
        return dataStore.data.map { preferences ->
            val currentPageKey = intPreferencesKey("${category}_current_page")
            val totalPagesKey = intPreferencesKey("${category}_total_pages")

            PaginationState(
                currentPage = preferences[currentPageKey] ?: 0,
                totalPages = preferences[totalPagesKey] ?: 0
            )
        }
    }

    override suspend fun savePaginationState(category: String, state: PaginationState) {
        dataStore.edit { preferences ->
            val currentPageKey = intPreferencesKey("${category}_current_page")
            val totalPagesKey = intPreferencesKey("${category}_total_pages")

            preferences[currentPageKey] = state.currentPage
            preferences[totalPagesKey] = state.totalPages
        }
    }

    override suspend fun clearAllPaginationState() {
        dataStore.edit { preferences ->
            // Remove all pagination-related keys
            val keysToRemove = preferences.asMap().keys.filter {
                it.name.endsWith("_current_page") || it.name.endsWith("_total_pages")
            }
            keysToRemove.forEach { key ->
                preferences.remove(key)
            }
        }
    }
}
