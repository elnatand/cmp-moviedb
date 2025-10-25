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
 * This ViewModel uses category abstraction.
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
     *
     * This implementation is truly open for extension:
     * - Adding a new category to MovieCategory enum requires ZERO changes here
     * - The map-based state automatically accommodates any number of categories
     */
    private fun observeMovies() {
        MovieCategory.entries.forEach { category ->
            viewModelScope.launch {
                moviesRepository.observeMovies(category).collect { movies ->
                    _uiState.update { currentState ->
                        val updatedMoviesMap = currentState.moviesByCategory + (category to movies)
                        currentState.copy(
                            moviesByCategory = updatedMoviesMap,
                            state = if (updatedMoviesMap.values.any { it.isNotEmpty() })
                                MoviesUiState.State.SUCCESS
                            else
                                MoviesUiState.State.LOADING
                        )
                    }
                }
            }
        }
    }

    /**
     * Loads the next page of movies for a specific category.
     *
     * Truly follows OCP - handles any category without code changes.
     * Implements proper error handling with different behaviors for initial vs pagination errors.
     *
     * This implementation is fully extensible:
     * - No when statements on category
     * - No hardcoded category checks
     * - Adding new categories requires ZERO changes to this method
     *
     * @param category The movie category to load the next page for
     */
    private fun loadNextPage(category: MovieCategory) {
        // Check if already loading for this category using map-based state
        if (_uiState.value.isLoading(category)) return

        viewModelScope.launch {
            // Set loading state for this category
            _uiState.update { currentState ->
                currentState.copy(
                    loadingByCategory = currentState.loadingByCategory + (category to true)
                )
            }

            when (val result = moviesRepository.loadMoviesNextPage(category)) {
                is AppResult.Error -> {
                    // Clear loading state
                    _uiState.update { currentState ->
                        currentState.copy(
                            loadingByCategory = currentState.loadingByCategory + (category to false)
                        )
                    }

                    // Get current movies for this category
                    val currentMovies = _uiState.value.getMovies(category)

                    // If we have movies, show snackbar; otherwise show error screen
                    if (currentMovies.isNotEmpty()) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = MoviesUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    // Clear loading state
                    _uiState.update { currentState ->
                        currentState.copy(
                            loadingByCategory = currentState.loadingByCategory + (category to false)
                        )
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
