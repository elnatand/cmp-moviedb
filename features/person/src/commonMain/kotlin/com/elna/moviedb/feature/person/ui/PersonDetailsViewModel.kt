package com.elna.moviedb.feature.person.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.person.PersonRepository
import com.elna.moviedb.core.model.AppResult
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

    /**
     * Loads person details for the given person ID and updates the UI state.
     *
     * Sets the state to `PersonUiState.Loading` then updates it to `PersonUiState.Success`
     * with the fetched data if the repository call succeeds, or to `PersonUiState.Error`
     * with an error message if the repository returns an error.
     *
     * @param personId The identifier of the person whose details should be fetched.
     */
    private fun getPersonDetails(personId: Int) {
        viewModelScope.launch {
            _uiState.value = PersonUiState.Loading
            when (val result = personRepository.getPersonDetails(personId)) {
                is AppResult.Error -> _uiState.value =
                    PersonUiState.Error(result.message)

                is AppResult.Success -> _uiState.value = PersonUiState.Success(result.data)
            }
        }
    }

    private fun retry() {
        getPersonDetails(personId)
    }
}