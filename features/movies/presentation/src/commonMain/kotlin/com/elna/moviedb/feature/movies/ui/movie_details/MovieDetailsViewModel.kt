package com.elna.moviedb.feature.movies.ui.movie_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.feature.movies.repositories.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.MovieDetailsEvent
import com.elna.moviedb.feature.movies.model.MovieDetailsUiState
import kotlinx.coroutines.Job
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

    private var detailsJob: Job? = null

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
        // Cancel any in-flight load so rapid retry taps don't race overlapping fetches.
        detailsJob?.cancel()
        detailsJob = viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            when (val result = moviesRepository.getMovieDetails(movieId)) {
                is AppResult.Success -> {
                    _uiState.value = MovieDetailsUiState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = MovieDetailsUiState.Error(result.type)
                }
            }
        }
    }

    private fun retry() {
        getMovieDetails(movieId)
    }
}