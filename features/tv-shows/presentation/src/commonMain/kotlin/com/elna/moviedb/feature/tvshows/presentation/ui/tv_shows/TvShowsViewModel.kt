package com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.feature.tvshows.domain.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * ViewModel following MVI (Model-View-Intent) pattern for TV Shows screen.
 * Implements Android's unidirectional data flow (UDF) pattern.
 *
 * UDF Components:
 * - Model: [com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiState] - Immutable state representing the UI
 * - View: TvShowsScreen - Renders the state and dispatches events
 * - Event: [TvShowsEvent] - User actions/events
 * - UI Action: [TvShowsUiAction] - One-time events (e.g., show snackbar)
 */
class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvShowsUiState(state = TvShowsUiState.State.LOADING))
    val uiState: StateFlow<TvShowsUiState> = _uiState.asStateFlow()

    private val _uiAction = Channel<TvShowsUiAction>(Channel.BUFFERED)
    val uiAction = _uiAction.receiveAsFlow()

    init {
        observeTvShows()
    }

    /**
     * Main entry point for handling user events.
     * All UI interactions should go through this method.
     */
    fun onEvent(event: TvShowsEvent) {
        when (event) {
            is TvShowsEvent.LoadNextPage -> loadNextPage(event.category)
            TvShowsEvent.Retry -> retry()
        }
    }

    /**
     * Observes TV shows for all categories defined in [TvShowCategory] enum.
     *
     * This implementation is truly open for extension:
     * - Adding a new category to TvShowCategory enum requires ZERO changes here
     * - The map-based state automatically accommodates any number of categories
     */
    private fun observeTvShows() {
        TvShowCategory.entries.forEach { category ->
            viewModelScope.launch {
                tvShowsRepository.observeTvShows(category).collect { tvShows ->
                    _uiState.update { currentState ->
                        val updatedTvShowsMap = currentState.tvShowsByCategory + (category to tvShows)
                        currentState.copy(
                            tvShowsByCategory = updatedTvShowsMap,
                            state = if (updatedTvShowsMap.values.any { it.isNotEmpty() })
                                TvShowsUiState.State.SUCCESS
                            else
                                TvShowsUiState.State.LOADING
                        )
                    }
                }
            }
        }
    }

    /**
     * Loads the next page of TV shows for a specific category.
     *
     * Truly follows OCP - handles any category without code changes.
     * Implements proper error handling with different behaviors for initial vs pagination errors.
     *
     * This implementation is fully extensible:
     * - No when statements on category
     * - No hardcoded category checks
     * - Adding new categories requires ZERO changes to this method
     *
     * @param category The TV show category to load the next page for
     */
    private fun loadNextPage(category: TvShowCategory) {
        // Check if already loading for this category using map-based state
        if (_uiState.value.isLoading(category)) return

        viewModelScope.launch {
            // Set loading state for this category
            _uiState.update { currentState ->
                currentState.copy(
                    loadingByCategory = currentState.loadingByCategory + (category to true)
                )
            }

            when (val result = tvShowsRepository.loadTvShowsNextPage(category)) {
                is AppResult.Error -> {
                    // Clear loading state
                    _uiState.update { currentState ->
                        currentState.copy(
                            loadingByCategory = currentState.loadingByCategory + (category to false)
                        )
                    }

                    // Get current TV shows for this category
                    val currentTvShows = _uiState.value.getTvShows(category)

                    // If we have TV shows, show snackbar; otherwise show error screen
                    if (currentTvShows.isNotEmpty()) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = TvShowsUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    // Clear loading state
                    _uiState.update { currentState ->
                        currentState.copy(
                            loadingByCategory = currentState.loadingByCategory + (category to false)
                        )
                    }
                    // Success - TV shows are already updated via observeTvShows()
                }
            }
        }
    }

    /**
     * Retries loading TV shows for all categories after an error.
     */
    private fun retry() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = TvShowsUiState.State.LOADING) }

            // Try to load all categories in parallel
            val results = TvShowCategory.entries.map { category ->
                async { tvShowsRepository.loadTvShowsNextPage(category) }
            }.awaitAll()

            // If any succeeded, consider it a success
            val hasSuccess = results.any { it is AppResult.Success }
            val allFailed = results.all { it is AppResult.Error }

            if (allFailed) {
                _uiState.update { it.copy(state = TvShowsUiState.State.ERROR) }
            } else if (hasSuccess) {
                // Success - state will be updated via observeTvShows()
            }
        }
    }
}