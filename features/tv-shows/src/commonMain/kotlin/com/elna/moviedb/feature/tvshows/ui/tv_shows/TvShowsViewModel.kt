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
import com.elna.moviedb.feature.tvshows.model.TvShowsSideEffect
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState

/**
 * ViewModel following MVI (Model-View-Intent) pattern for TV Shows screen.
 * Implements Android's unidirectional data flow (UDF) pattern.
 *
 * UDF Components:
 * - Model: [TvShowsUiState] - Immutable state representing the UI
 * - View: TvShowsScreen - Renders the state and dispatches events
 * - Event: [TvShowsEvent] - User actions/events
 * - Side Effects: [TvShowsSideEffect] - One-time events (e.g., show snackbar)
 */
class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvShowsUiState(state = TvShowsUiState.State.LOADING))
    val uiState: StateFlow<TvShowsUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<TvShowsSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        observeTvShows()
        observePaginationErrors()
    }

    /**
     * Main entry point for handling user events.
     * All UI interactions should go through this method.
     */
    fun onEvent(event: TvShowsEvent) {
        when (event) {
            TvShowsEvent.LoadNextPage -> loadNextPage()
            TvShowsEvent.Retry -> retry()
        }
    }

    private fun observeTvShows() {
        viewModelScope.launch {
            tvShowsRepository.observeAllTvShows().collect { response ->
                when (response) {
                    is AppResult.Error -> _uiState.update { currentState ->
                        currentState.copy(state = TvShowsUiState.State.ERROR)
                    }

                    is AppResult.Success -> _uiState.update { currentState ->
                        currentState.copy(
                            state = TvShowsUiState.State.SUCCESS,
                            tvShows = response.data
                        )
                    }
                }
            }
        }
    }

    private fun loadNextPage() {
        viewModelScope.launch {
            tvShowsRepository.loadNextPage()
        }
    }

    private fun observePaginationErrors() {
        viewModelScope.launch {
            tvShowsRepository.paginationErrors.collect { errorMessage ->
                _sideEffect.send(TvShowsSideEffect.ShowPaginationError(errorMessage))
            }
        }
    }

    private fun retry() {
        viewModelScope.launch {
            tvShowsRepository.loadNextPage()
        }
    }
}