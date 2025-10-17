package com.elna.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.MovieCategory
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
 * This ViewModel follows the Open/Closed Principle by using category abstraction.
 * New movie categories can be added to [MovieCategory] enum without modifying this class.
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
     *
     * Follows OCP - new categories don't require new event handlers.
     */
    fun onEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.LoadNextPage -> loadNextPage(event.category)
            MoviesEvent.Retry -> retry()
        }
    }

    /**
     * Observes movies for all categories defined in [MovieCategory] enum.
     *
     * Follows OCP - automatically handles all categories without code changes.
     * Each category is observed in a separate coroutine for independent updates.
     */
    private fun observeMovies() {
        MovieCategory.entries.forEach { category ->
            viewModelScope.launch {
                moviesRepository.observeMovies(category).collect { movies ->
                    _uiState.update { currentState ->
                        val updated = when (category) {
                            MovieCategory.POPULAR -> currentState.copy(popularMovies = movies)
                            MovieCategory.TOP_RATED -> currentState.copy(topRatedMovies = movies)
                            MovieCategory.NOW_PLAYING -> currentState.copy(nowPlayingMovies = movies)
                        }
                        updated.copy(
                            state = if (updated.hasAnyData) MoviesUiState.State.SUCCESS else MoviesUiState.State.LOADING,
                        )
                    }
                }
            }
        }
    }

    /**
     * Loads the next page of movies for a specific category.
     *
     * Follows OCP - handles any category without code changes.
     * Implements proper error handling with different behaviors for initial vs pagination errors.
     *
     * @param category The movie category to load the next page for
     */
    private fun loadNextPage(category: MovieCategory) {
        // Check if already loading for this category
        val isLoading = when (category) {
            MovieCategory.POPULAR -> _uiState.value.isLoadingPopular
            MovieCategory.TOP_RATED -> _uiState.value.isLoadingTopRated
            MovieCategory.NOW_PLAYING -> _uiState.value.isLoadingNowPlaying
        }
        if (isLoading) return

        viewModelScope.launch {
            // Set loading state for this category
            _uiState.update {
                when (category) {
                    MovieCategory.POPULAR -> it.copy(isLoadingPopular = true)
                    MovieCategory.TOP_RATED -> it.copy(isLoadingTopRated = true)
                    MovieCategory.NOW_PLAYING -> it.copy(isLoadingNowPlaying = true)
                }
            }

            when (val result = moviesRepository.loadMoviesNextPage(category)) {
                is AppResult.Error -> {
                    // Clear loading state
                    _uiState.update {
                        when (category) {
                            MovieCategory.POPULAR -> it.copy(isLoadingPopular = false)
                            MovieCategory.TOP_RATED -> it.copy(isLoadingTopRated = false)
                            MovieCategory.NOW_PLAYING -> it.copy(isLoadingNowPlaying = false)
                        }
                    }

                    // Get current movies for this category
                    val currentMovies = when (category) {
                        MovieCategory.POPULAR -> _uiState.value.popularMovies
                        MovieCategory.TOP_RATED -> _uiState.value.topRatedMovies
                        MovieCategory.NOW_PLAYING -> _uiState.value.nowPlayingMovies
                    }

                    // If we have movies, show snackbar; otherwise show error screen
                    if (currentMovies.isNotEmpty()) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    // Clear loading state
                    _uiState.update {
                        when (category) {
                            MovieCategory.POPULAR -> it.copy(isLoadingPopular = false)
                            MovieCategory.TOP_RATED -> it.copy(isLoadingTopRated = false)
                            MovieCategory.NOW_PLAYING -> it.copy(isLoadingNowPlaying = false)
                        }
                    }
                    // Success - movies are already updated via observeMovies()
                }
            }
        }
    }

    /**
     * Retries loading movies for all categories after an error.
     *
     * Follows OCP - automatically retries all categories defined in [MovieCategory] enum.
     * Loads all categories in parallel for better performance.
     */
    private fun retry() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = MoviesUiState.State.LOADING) }

            // Try to load all categories in parallel
            val results = MovieCategory.entries.map { category ->
                async { moviesRepository.loadMoviesNextPage(category) }
            }.awaitAll()

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
