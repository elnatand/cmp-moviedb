package com.elna.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesUiAction
import com.elna.moviedb.feature.movies.model.MoviesUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
 * - UI Action: [MoviesUiAction] - One-time events (e.g., show snackbar)
 */
class MoviesViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState(state = MoviesUiState.State.LOADING))
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private val _uiAction = Channel<MoviesUiAction>(Channel.BUFFERED)
    val uiAction = _uiAction.receiveAsFlow()

    init {
        observeMovies()
    }

    /**
     * Main entry point for handling user events.
     * All UI interactions should go through this method.
     */
    fun onEvent(event: MoviesEvent) {
        when (event) {
            MoviesEvent.LoadNextPagePopular -> loadNextPagePopular()
            MoviesEvent.LoadNextPageTopRated -> loadNextPageTopRated()
            MoviesEvent.LoadNextPageNowPlaying -> loadNextPageNowPlaying()
            MoviesEvent.Retry -> retry()
        }
    }

    private fun observeMovies() {
        // Observe popular movies
        viewModelScope.launch {
            moviesRepository.observePopularMovies().collect { movies ->
                _uiState.update { currentState ->
                    val updated = currentState.copy(popularMovies = movies)
                    updated.copy(
                        state = if (updated.hasAnyData) MoviesUiState.State.SUCCESS else MoviesUiState.State.LOADING,
                    )
                }
            }
        }

        // Observe top rated movies
        viewModelScope.launch {
            moviesRepository.observeTopRatedMovies().collect { movies ->
                _uiState.update { currentState ->
                    val updated = currentState.copy(topRatedMovies = movies)
                    updated.copy(
                        state = if (updated.hasAnyData) MoviesUiState.State.SUCCESS else MoviesUiState.State.LOADING,
                    )
                }
            }
        }

        // Observe now playing movies
        viewModelScope.launch {
            moviesRepository.observeNowPlayingMovies().collect { movies ->
                _uiState.update { currentState ->
                    val updated = currentState.copy(nowPlayingMovies = movies)
                    updated.copy(
                        state = if (updated.hasAnyData) MoviesUiState.State.SUCCESS else MoviesUiState.State.LOADING,
                    )
                }
            }
        }
    }

    private fun loadNextPagePopular() {
        if (_uiState.value.isLoadingPopular) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPopular = true) }
            when (val result = moviesRepository.loadPopularMoviesNextPage()) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingPopular = false) }
                    // If we have movies, show snackbar; otherwise show error screen
                    if (_uiState.value.popularMovies.isNotEmpty()) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingPopular = false) }
                    // Success - movies are already updated via observeMovies()
                }
            }
        }
    }

    private fun loadNextPageTopRated() {
        if (_uiState.value.isLoadingTopRated) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTopRated = true) }
            when (val result = moviesRepository.loadTopRatedMoviesNextPage()) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingTopRated = false) }
                    if (_uiState.value.topRatedMovies.isNotEmpty()) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingTopRated = false) }
                }
            }
        }
    }

    private fun loadNextPageNowPlaying() {
        if (_uiState.value.isLoadingNowPlaying) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNowPlaying = true) }
            when (val result = moviesRepository.loadNowPlayingMoviesNextPage()) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingNowPlaying = false) }
                    if (_uiState.value.nowPlayingMovies.isNotEmpty()) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingNowPlaying = false) }
                }
            }
        }
    }

    private fun retry() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = MoviesUiState.State.LOADING) }

            // Try to load all three categories
            val results = awaitAll(
                async { moviesRepository.loadPopularMoviesNextPage() },
                async { moviesRepository.loadTopRatedMoviesNextPage() },
                async { moviesRepository.loadNowPlayingMoviesNextPage() },
            )

            // If any succeeded, consider it a success
            val hasSuccess = results.any { it is AppResult.Success }
            val allFailed = results.all { it is AppResult.Error }

            if (allFailed) {
                _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
            } else if (hasSuccess) {
                // Success - state will be updated via observeMovies()
            }
        }
    }
}
