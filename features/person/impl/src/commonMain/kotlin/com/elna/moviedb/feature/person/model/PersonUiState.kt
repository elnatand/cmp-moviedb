package com.elna.moviedb.feature.person.model

import com.elna.moviedb.core.model.PersonDetails

sealed interface PersonUiState {
    data object Loading : PersonUiState
    data class Success(val person: PersonDetails) : PersonUiState
    data class Error(val message: String) : PersonUiState
}
