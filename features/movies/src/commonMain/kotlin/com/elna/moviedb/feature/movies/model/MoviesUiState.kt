package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.model.Movie

/**
 * Represents the UI state for the Movies screen.
 * Contains three separate lists of movies and their individual pagination states.
 *
 * @property state Overall screen state (LOADING, ERROR, SUCCESS)
 * @property popularMovies List of popular movies
 * @property topRatedMovies List of top-rated movies
 * @property nowPlayingMovies List of now playing movies
 * @property isLoadingPopular True when loading more popular movies (pagination)
 * @property isLoadingTopRated True when loading more top-rated movies (pagination)
 * @property isLoadingNowPlaying True when loading more now playing movies (pagination)
 */
data class MoviesUiState(
    val state: State,
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val isLoadingPopular: Boolean = false,
    val isLoadingTopRated: Boolean = false,
    val isLoadingNowPlaying: Boolean = false
) {

    val hasAnyData: Boolean
        get() = popularMovies.isNotEmpty() || topRatedMovies.isNotEmpty() || nowPlayingMovies.isNotEmpty()

    enum class State {
        LOADING, ERROR, SUCCESS
    }
}


