package com.elna.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesSideEffect
import com.elna.moviedb.feature.movies.model.MoviesUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for Movies screen.
 * Implements Android's unidirectional data flow (UDF) pattern.
 *
 * UDF Components:
 * - Model: [MoviesUiState] - Immutable state representing the UI
 * - View: MoviesScreen - Renders the state and dispatches events
 * - Event: [MoviesEvent] - User actions/events
 * - Side Effects: [MoviesSideEffect] - One-time events (e.g., show snackbar)
 */
class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState(state = MoviesUiState.State.LOADING))
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<MoviesSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        observeMovies()
        observePaginationErrors()
    }

    /**
     * Main entry point for handling user events.
     * All UI interactions should go through this method.
     */
    fun onEvent(event: MoviesEvent) {
        when (event) {
            MoviesEvent.LoadNextPage -> loadNextPage()
            MoviesEvent.Retry -> retry()
        }
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

    private fun observePaginationErrors() {
        viewModelScope.launch {
            moviesRepository.paginationErrors.collect { errorMessage ->
                _sideEffect.send(MoviesSideEffect.ShowPaginationError(errorMessage))
            }
        }
    }

    private fun loadNextPage() {
        viewModelScope.launch {
            moviesRepository.loadNextPage()
        }
    }

    private fun retry() {
        viewModelScope.launch {
            moviesRepository.loadNextPage()
        }
    }
}
