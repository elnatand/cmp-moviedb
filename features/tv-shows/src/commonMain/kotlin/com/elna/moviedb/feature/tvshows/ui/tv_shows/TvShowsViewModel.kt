package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.feature.tvshows.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * ViewModel following MVI (Model-View-Intent) pattern for TV Shows screen.
 * Implements Android's unidirectional data flow (UDF) pattern.
 *
 * UDF Components:
 * - Model: [TvShowsUiState] - Immutable state representing the UI
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
     */
    private fun observeTvShows() {
        TvShowCategory.entries.forEach { category ->
            viewModelScope.launch {
                tvShowsRepository.observeTvShows(category).collect { tvShows ->
                    _uiState.update { currentState ->
                        val updated = when (category) {
                            TvShowCategory.POPULAR -> currentState.copy(popularTvShows = tvShows)
                            TvShowCategory.ON_THE_AIR -> currentState.copy(onTheAirTvShows = tvShows)
                            TvShowCategory.TOP_RATED -> currentState.copy(topRatedTvShows = tvShows)
                        }
                        updated.copy(
                            state = if (updated.hasAnyData) TvShowsUiState.State.SUCCESS else TvShowsUiState.State.LOADING,
                        )
                    }
                }
            }
        }
    }

    /**
     * Loads the next page of TV shows for a specific category.
     *
     * @param category The TV show category to load the next page for
     */
    private fun loadNextPage(category: TvShowCategory) {
        // Check if already loading for this category
        val isLoading = when (category) {
            TvShowCategory.POPULAR -> _uiState.value.isLoadingPopular
            TvShowCategory.ON_THE_AIR -> _uiState.value.isLoadingOnTheAir
            TvShowCategory.TOP_RATED -> _uiState.value.isLoadingTopRated
        }
        if (isLoading) return

        viewModelScope.launch {
            // Set loading state for this category
            _uiState.update {
                when (category) {
                    TvShowCategory.POPULAR -> it.copy(isLoadingPopular = true)
                    TvShowCategory.ON_THE_AIR -> it.copy(isLoadingOnTheAir = true)
                    TvShowCategory.TOP_RATED -> it.copy(isLoadingTopRated = true)
                }
            }

            when (val result = tvShowsRepository.loadTvShowsNextPage(category)) {
                is AppResult.Error -> {
                    // Clear loading state
                    _uiState.update {
                        when (category) {
                            TvShowCategory.POPULAR -> it.copy(isLoadingPopular = false)
                            TvShowCategory.ON_THE_AIR -> it.copy(isLoadingOnTheAir = false)
                            TvShowCategory.TOP_RATED -> it.copy(isLoadingTopRated = false)
                        }
                    }

                    // Get current TV shows for this category
                    val currentTvShows = when (category) {
                        TvShowCategory.POPULAR -> _uiState.value.popularTvShows
                        TvShowCategory.ON_THE_AIR -> _uiState.value.onTheAirTvShows
                        TvShowCategory.TOP_RATED -> _uiState.value.topRatedTvShows
                    }

                    // If we have TV shows, show snackbar; otherwise show error screen
                    if (currentTvShows.isNotEmpty()) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = TvShowsUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    // Clear loading state
                    _uiState.update {
                        when (category) {
                            TvShowCategory.POPULAR -> it.copy(isLoadingPopular = false)
                            TvShowCategory.ON_THE_AIR -> it.copy(isLoadingOnTheAir = false)
                            TvShowCategory.TOP_RATED -> it.copy(isLoadingTopRated = false)
                        }
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