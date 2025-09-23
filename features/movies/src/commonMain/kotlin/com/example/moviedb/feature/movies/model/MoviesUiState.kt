package com.example.moviedb.feature.movies.model

import com.example.moviedb.core.model.Movie

data class MoviesUiState(
    val state: State,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) {
    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Success(val movies: List<Movie>) : State
    }
}


