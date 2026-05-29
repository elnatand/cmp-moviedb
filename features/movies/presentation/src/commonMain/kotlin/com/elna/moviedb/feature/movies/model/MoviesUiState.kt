package com.elna.moviedb.feature.movies.model

import androidx.compose.runtime.Immutable

/**
 * Represents the UI state for the Movies screen.
 * Uses map-based state management.
 *
 * This design allows adding new movie categories without modifying this class.
 * Simply add the new category to the MovieCategory enum, and it will automatically
 * be supported by this state structure.
 *
 * Marked [Immutable] because the Map/Set fields are read-only types Compose otherwise
 * treats as unstable, which would make this state (and the whole screen) non-skippable.
 * The contract holds: these collections are only ever replaced via copy(), never mutated
 * in place.
 *
 * @property state Overall screen state (LOADING, ERROR, SUCCESS)
 * @property moviesByCategory Map of movie categories to their respective movie lists
 * @property loadingByCategory Map of movie categories to their pagination loading states
 * @property failedCategories Categories whose most recent load failed (for inline section errors)
 */
@Immutable
data class MoviesUiState(
    val state: State,
    val moviesByCategory: Map<MovieCategory, List<Movie>> = emptyMap(),
    val loadingByCategory: Map<MovieCategory, Boolean> = emptyMap(),
    val failedCategories: Set<MovieCategory> = emptySet(),
    val isRefreshing: Boolean = false
) {

    /**
     * Returns true if any category has data.
     */
    val hasAnyData: Boolean
        get() = moviesByCategory.values.any { it.isNotEmpty() }

    /**
     * Checks if a specific category's most recent load failed.
     *
     * Used to render an inline error + retry for an empty section instead of a loader
     * that would otherwise spin forever (the full-screen error only covers the case where
     * no category has any data).
     */
    fun hasFailed(category: MovieCategory): Boolean = category in failedCategories

    /**
     * Gets movies for a specific category.
     *
     * @param category The movie category to retrieve
     * @return List of movies for the category, or empty list if category not found
     */
    fun getMovies(category: MovieCategory): List<Movie> =
        moviesByCategory[category] ?: emptyList()

    /**
     * Checks if a specific category is currently loading more movies (pagination).
     *
     * @param category The movie category to check
     * @return True if the category is loading, false otherwise
     */
    fun isLoading(category: MovieCategory): Boolean =
        loadingByCategory[category] ?: false

    enum class State {
        LOADING, ERROR, SUCCESS
    }
}


