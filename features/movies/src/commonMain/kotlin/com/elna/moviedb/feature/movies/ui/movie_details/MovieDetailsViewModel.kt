package com.elna.moviedb.feature.movies.ui.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.feature.movies.model.MovieDetailsEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for Movie Details screen.
 *
 * MVI Components:
 * - Model: [MovieDetailsUiState] - Immutable state representing the UI
 * - View: MovieDetailsScreen - Renders the state and dispatches intents
 * - Intent: [MovieDetailsEvent] - User actions/intentions
 */
class MovieDetailsViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        getMovieDetails(movieId)
    }

    /**
     * Main entry point for handling user intents.
     * All UI interactions should go through this method.
     */
    fun onEvent(intent: MovieDetailsEvent) {
        when (intent) {
            MovieDetailsEvent.Retry -> retry()
        }
    }

    private fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            when (val result = moviesRepository.getMovieDetails(movieId)) {
                is AppResult.Success -> {
                    _uiState.value = MovieDetailsUiState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = MovieDetailsUiState.Error(result.message)
                }
            }
        }
    }

    private fun retry() {
        getMovieDetails(movieId)
    }

    sealed interface MovieDetailsUiState {
        data object Loading : MovieDetailsUiState
        data class Success(val movieDetails: MovieDetails) : MovieDetailsUiState
        data class Error(val message: String) : MovieDetailsUiState
    }
}