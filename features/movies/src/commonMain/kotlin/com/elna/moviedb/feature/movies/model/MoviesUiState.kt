package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.model.Movie

data class MoviesUiState(
    val state: State,
    val movies: List<Movie> = emptyList(),
) {
    enum class State {
        LOADING, ERROR, SUCCESS
    }
}


