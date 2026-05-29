package com.elna.moviedb.feature.movies.ui.movies


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.feature.movies.repositories.MoviesRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesUiAction
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

    // Categories whose most recent load attempt failed. The full-screen error is only
    // shown when there is no data to display anywhere (see recomputeScreenState). Confined
    // to viewModelScope's main dispatcher, so it's never mutated concurrently.
    private val erroredCategories = mutableSetOf<MovieCategory>()

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
            MoviesEvent.Refresh -> refresh()
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
                var initialLoadRequested = false
                moviesRepository.observeMovies(category).collect { movies ->
                    // Repository observe is passive; trigger the first load here (once)
                    // when the cache is empty. Keeps query/command responsibilities split.
                    if (!initialLoadRequested && movies.isEmpty()) {
                        initialLoadRequested = true
                        loadNextPage(category)
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            moviesByCategory = currentState.moviesByCategory + (category to movies)
                        )
                    }
                    // Overall screen state is derived, not set per-emission, so a single
                    // category can't clobber another category's state.
                    recomputeScreenState()
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
            setCategoryLoading(category, true)

            when (val result = moviesRepository.loadMoviesNextPage(category)) {
                is AppResult.Error -> {
                    setCategoryLoading(category, false)
                    erroredCategories += category

                    // A failed load only blanks the screen when there's nothing to show
                    // anywhere. If any category already has data, this is a pagination /
                    // refresh hiccup — surface it as a snackbar and keep the content.
                    if (_uiState.value.hasAnyData) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError)
                    }
                    recomputeScreenState()
                }

                is AppResult.Success -> {
                    setCategoryLoading(category, false)
                    erroredCategories -= category
                    recomputeScreenState()
                    // Loaded movies arrive via observeMovies().
                }
            }
        }
    }

    private fun setCategoryLoading(category: MovieCategory, isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                loadingByCategory = currentState.loadingByCategory + (category to isLoading)
            )
        }
    }

    /**
     * Derives the overall screen state from the current data and error tracking.
     *
     * The full-screen error is shown only when a load has failed AND no category has any
     * data to display. As soon as any category has content, errors become non-blocking
     * (surfaced via snackbar), so one failing category never hides others' data.
     */
    private fun recomputeScreenState() {
        _uiState.update { currentState ->
            val newState = if (!currentState.hasAnyData && erroredCategories.isNotEmpty()) {
                MoviesUiState.State.ERROR
            } else {
                MoviesUiState.State.SUCCESS
            }
            currentState.copy(state = newState)
        }
    }

    /**
     * Retries loading movies for all categories after an error.
     *
     * Follows OCP - automatically retries all categories defined in [MovieCategory] enum.
     * Loads all categories in parallel for better performance.
     */
    private fun retry() {
        // Clear stale failures and let each category reload independently. The screen
        // state (and any per-category snackbar) is then derived by loadNextPage, so the
        // result naturally reflects "all failed" → error vs "some succeeded" → content.
        erroredCategories.clear()
        recomputeScreenState()
        MovieCategory.entries.forEach { category -> loadNextPage(category) }
    }

    /**
     * Refreshes all movie categories by clearing cache and reloading.
     *
     * This is triggered by the pull-to-refresh gesture.
     * Shows a refresh indicator while maintaining the current content visible.
     */
    private fun refresh() {
        // Prevent duplicate refresh operations
        if (_uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            when (moviesRepository.clearAndReload()) {
                is AppResult.Error -> {
                    // The cache was just cleared. If the reload failed wholesale, record the
                    // failures so the derived screen state shows an error instead of spinning
                    // on empty per-section loaders forever.
                    erroredCategories.addAll(MovieCategory.entries)
                    if (_uiState.value.hasAnyData) {
                        _uiAction.send(MoviesUiAction.ShowPaginationError)
                    }
                }

                is AppResult.Success -> erroredCategories.clear()
            }
            recomputeScreenState()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}
