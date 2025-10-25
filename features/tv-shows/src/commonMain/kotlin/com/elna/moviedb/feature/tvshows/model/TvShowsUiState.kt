package com.elna.moviedb.feature.tvshows.model

import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowCategory

/**
 * Represents the UI state for the TV Shows screen.
 * Uses map-based state management.
 *
 * This design allows adding new TV show categories without modifying this class.
 * Simply add the new category to the TvShowCategory enum, and it will automatically
 * be supported by this state structure.
 *
 * @property state Overall screen state (LOADING, ERROR, SUCCESS)
 * @property tvShowsByCategory Map of TV show categories to their respective TV show lists
 * @property loadingByCategory Map of TV show categories to their pagination loading states
 */
data class TvShowsUiState(
    val state: State,
    val tvShowsByCategory: Map<TvShowCategory, List<TvShow>> = emptyMap(),
    val loadingByCategory: Map<TvShowCategory, Boolean> = emptyMap()
) {

    /**
     * Returns true if any category has data.
     */
    val hasAnyData: Boolean
        get() = tvShowsByCategory.values.any { it.isNotEmpty() }

    /**
     * Gets TV shows for a specific category.
     *
     * @param category The TV show category to retrieve
     * @return List of TV shows for the category, or empty list if category not found
     */
    fun getTvShows(category: TvShowCategory): List<TvShow> =
        tvShowsByCategory[category] ?: emptyList()

    /**
     * Checks if a specific category is currently loading more TV shows (pagination).
     *
     * @param category The TV show category to check
     * @return True if the category is loading, false otherwise
     */
    fun isLoading(category: TvShowCategory): Boolean =
        loadingByCategory[category] ?: false

    enum class State {
        LOADING, ERROR, SUCCESS
    }
}