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
import com.elna.moviedb.feature.tvshows.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

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
            TvShowsEvent.LoadNextPagePopular -> loadNextPagePopular()
            TvShowsEvent.LoadNextPageTopRated -> loadNextPageTopRated()
            TvShowsEvent.LoadNextPageOnTheAir -> loadNextPageOnTheAir()
            TvShowsEvent.Retry -> retry()
        }
    }

    private fun observeTvShows() {
        // Observe popular TV shows
        viewModelScope.launch {
            tvShowsRepository.observePopularTvShows().collect { tvShows ->
                _uiState.update { currentState ->
                    currentState.copy(
                        state = if (currentState.hasAnyData) TvShowsUiState.State.SUCCESS else TvShowsUiState.State.LOADING,
                        popularTvShows = tvShows
                    )
                }
            }
        }

        // Observe top-rated TV shows
        viewModelScope.launch {
            tvShowsRepository.observeTopRatedTvShows().collect { tvShows ->
                _uiState.update { currentState ->
                    currentState.copy(
                        state = if (currentState.hasAnyData) TvShowsUiState.State.SUCCESS else TvShowsUiState.State.LOADING,
                        topRatedTvShows = tvShows
                    )
                }
            }
        }

        // Observe on-the-air TV shows
        viewModelScope.launch {
            tvShowsRepository.observeOnTheAirTvShows().collect { tvShows ->
                _uiState.update { currentState ->
                    currentState.copy(
                        state = if (currentState.hasAnyData) TvShowsUiState.State.SUCCESS else TvShowsUiState.State.LOADING,
                        onTheAirTvShows = tvShows
                    )
                }
            }
        }
    }

    private fun loadNextPagePopular() {
        if (_uiState.value.isLoadingPopular) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPopular = true) }
            when (val result = tvShowsRepository.loadPopularTvShowsNextPage()) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingPopular = false) }
                    // If we have TV shows, show snackbar; otherwise show error screen
                    if (_uiState.value.popularTvShows.isNotEmpty()) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = TvShowsUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingPopular = false) }
                    // Success - TV shows are already updated via observeTvShows()
                }
            }
        }
    }

    private fun loadNextPageTopRated() {
        if (_uiState.value.isLoadingTopRated) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTopRated = true) }
            when (val result = tvShowsRepository.loadTopRatedTvShowsNextPage()) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingTopRated = false) }
                    if (_uiState.value.topRatedTvShows.isNotEmpty()) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError(result.message))
                    } else {
                        _uiState.update { it.copy(state = TvShowsUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingTopRated = false) }
                }
            }
        }
    }

    private fun loadNextPageOnTheAir() {
        if (_uiState.value.isLoadingOnTheAir) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOnTheAir = true) }
            when (val result = tvShowsRepository.loadOnTheAirTvShowsNextPage()) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingOnTheAir = false) }
                    if (_uiState.value.onTheAirTvShows.isNotEmpty()) {
                        _uiAction.send(TvShowsUiAction.ShowPaginationError(result.message))
                    }else {
                        _uiState.update { it.copy(state = TvShowsUiState.State.ERROR) }
                    }
                }

                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoadingOnTheAir = false) }
                }
            }
        }
    }

    private fun retry() {
        viewModelScope.launch {
            _uiState.update { it.copy(state = TvShowsUiState.State.LOADING) }

            // Try to load all three categories
            val results = awaitAll(
                async { tvShowsRepository.loadPopularTvShowsNextPage() },
                async { tvShowsRepository.loadTopRatedTvShowsNextPage() },
                async { tvShowsRepository.loadOnTheAirTvShowsNextPage() },
            )

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