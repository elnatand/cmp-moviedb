package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory

/**
 * Represents the UI state for the Movies screen.
 * Uses map-based state management following the Open/Closed Principle.
 *
 * This design allows adding new movie categories without modifying this class.
 * Simply add the new category to the MovieCategory enum, and it will automatically
 * be supported by this state structure.
 *
 * @property state Overall screen state (LOADING, ERROR, SUCCESS)
 * @property moviesByCategory Map of movie categories to their respective movie lists
 * @property loadingByCategory Map of movie categories to their pagination loading states
 */
data class MoviesUiState(
    val state: State,
    val moviesByCategory: Map<MovieCategory, List<Movie>> = emptyMap(),
    val loadingByCategory: Map<MovieCategory, Boolean> = emptyMap()
) {

    /**
     * Returns true if any category has data.
     */
    val hasAnyData: Boolean
        get() = moviesByCategory.values.any { it.isNotEmpty() }

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


