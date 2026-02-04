package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.model.MovieDetails

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState
    data class Success(val movieDetails: MovieDetails) : MovieDetailsUiState
    data class Error(val message: String) : MovieDetailsUiState
}
