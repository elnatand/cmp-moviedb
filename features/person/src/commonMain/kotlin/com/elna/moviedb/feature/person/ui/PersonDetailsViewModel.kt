package com.elna.moviedb.feature.person.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.person.PersonRepository
import com.elna.moviedb.feature.person.model.PersonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonDetailsViewModel(
    private val personId: Int,
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonUiState>(PersonUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getPersonDetails(personId)
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

    fun retry() {
        getPersonDetails(personId)
    }
}