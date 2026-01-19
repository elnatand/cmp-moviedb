package com.elna.moviedb.feature.person.model

sealed interface PersonUiState {
    data object Loading : PersonUiState
    data class Success(val person: PersonDetails) : PersonUiState
    data class Error(val message: String) : PersonUiState
}