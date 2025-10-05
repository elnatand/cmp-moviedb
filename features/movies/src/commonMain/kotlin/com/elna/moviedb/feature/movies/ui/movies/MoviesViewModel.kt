package com.elna.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.MoviesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState(state = MoviesUiState.State.LOADING))
    val uiState = _uiState.asStateFlow()

    /**
     * Exposes pagination errors for UI to display as snackbars.
     */
    val paginationErrors = moviesRepository.paginationErrors

    init {
        observeMovies()
    }

    private fun observeMovies() {
        viewModelScope.launch {
            moviesRepository.observeAllMovies().collect { response ->
                when (response) {
                    is AppResult.Error -> _uiState.update { currentState ->
                        currentState.copy(state = MoviesUiState.State.ERROR)
                    }

                    is AppResult.Success -> _uiState.update { currentState ->
                        currentState.copy(
                            state = MoviesUiState.State.SUCCESS,
                            movies = response.data
                        )
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            moviesRepository.loadNextPage()
        }
    }
}
