package com.elna.moviedb.feature.tvshows.ui.tv_show_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.feature.tvshows.model.TvShowDetailsIntent
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
 * - Intent: [TvShowDetailsIntent] - User actions/intentions
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
    fun handleIntent(intent: TvShowDetailsIntent) {
        when (intent) {
            TvShowDetailsIntent.Retry -> retry()
        }
    }

    private fun getTvShowDetails(tvShowId: Int) {
        viewModelScope.launch {
            _uiState.value = TvShowDetailsUiState.Loading
            try {
                val tvShowDetails = tvShowsRepository.getTvShowDetails(tvShowId)
                _uiState.value = TvShowDetailsUiState.Success(tvShowDetails)
            } catch (e: Exception) {
                _uiState.value = TvShowDetailsUiState.Error(e.message ?: "Unknown error occurred")
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
