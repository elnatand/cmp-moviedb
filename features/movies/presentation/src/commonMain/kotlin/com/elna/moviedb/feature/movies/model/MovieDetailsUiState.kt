package com.elna.moviedb.feature.movies.model

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState
    data class Success(val movieDetails: MovieDetails) : MovieDetailsUiState
    data class Error(val message: String) : MovieDetailsUiState
}
