package com.elna.moviedb.feature.person.presentation.model

import com.elna.moviedb.feature.person.domain.model.PersonDetails

sealed interface PersonUiState {
    data object Loading : PersonUiState
    data class Success(val person: PersonDetails) : PersonUiState
    data class Error(val message: String) : PersonUiState
}