package com.elna.moviedb.feature.person.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.person.PersonRepository
import com.elna.moviedb.feature.person.model.PersonDetailsEvent
import com.elna.moviedb.feature.person.model.PersonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for Person Details screen.
 *
 * MVI Components:
 * - Model: [PersonUiState] - Immutable state representing the UI
 * - View: PersonDetailsScreen - Renders the state and dispatches intents
 * - Intent: [PersonDetailsEvent] - User actions/intentions
 */
class PersonDetailsViewModel(
    private val personId: Int,
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonUiState>(PersonUiState.Loading)
    val uiState: StateFlow<PersonUiState> = _uiState.asStateFlow()

    init {
        getPersonDetails(personId)
    }

    /**
     * Main entry point for handling user intents.
     * All UI interactions should go through this method.
     */
    fun onEvent(intent: PersonDetailsEvent) {
        when (intent) {
            PersonDetailsEvent.Retry -> retry()
        }
    }

    private fun getPersonDetails(personId: Int) {
        viewModelScope.launch {
            _uiState.value = PersonUiState.Loading
            try {
                val personDetails = personRepository.getPersonDetails(personId)
                _uiState.value = PersonUiState.Success(personDetails)
            } catch (e: Exception) {
                _uiState.value = PersonUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun retry() {
        getPersonDetails(personId)
    }
}