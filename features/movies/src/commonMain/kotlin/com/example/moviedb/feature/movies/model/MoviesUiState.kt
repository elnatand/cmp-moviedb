package com.example.moviedb.feature.movies.model

import com.example.moviedb.core.model.Movie

data class MoviesUiState(
    val state: State,
    val currentPage: Int = 1,
    val movies: List<Movie> = emptyList(),
) {
    enum class State {
        LOADING, ERROR, SUCCESS
    }
}


