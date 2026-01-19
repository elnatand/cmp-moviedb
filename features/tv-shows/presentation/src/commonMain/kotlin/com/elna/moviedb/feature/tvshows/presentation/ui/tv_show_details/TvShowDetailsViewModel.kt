package com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.feature.tvshows.domain.repositories.TvShowsRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for TV Show Details screen.
 *
 * MVI Components:
 * - Model: [TvShowDetailsUiState] - Immutable state representing the UI
 * - View: TvShowDetailsScreen - Renders the state and dispatches intents
 * - Intent: [com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent] - User actions/intentions
 */
class TvShowDetailsViewModel(
    private val tvShowId: Int,
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvShowDetailsUiState>(TvShowDetailsUiState.Loading)
    val uiState: StateFlow<TvShowDetailsUiState> = _uiState.asStateFlow()

    init {
        getTvShowDetails(tvShowId)
    }

    /**
     * Main entry point for handling user intents.
     * All UI interactions should go through this method.
     */
    fun onEvent(intent: com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent) {
        when (intent) {
            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent.Retry -> retry()
        }
    }

    private fun getTvShowDetails(tvShowId: Int) {
        viewModelScope.launch {
            _uiState.value = TvShowDetailsUiState.Loading
            val result = tvShowsRepository.getTvShowDetails(tvShowId)
            when (result) {
                is AppResult.Error -> _uiState.value =
                    TvShowDetailsUiState.Error(result.message)

                is AppResult.Success -> _uiState.value =
                    TvShowDetailsUiState.Success(result.data)
            }
        }
    }

    private fun retry() {
        getTvShowDetails(tvShowId)
    }

    sealed interface TvShowDetailsUiState {
        data object Loading : TvShowDetailsUiState
        data class Success(val tvShowDetails: TvShowDetails) : TvShowDetailsUiState
        data class Error(val message: String) : TvShowDetailsUiState
    }
}
