package com.elna.moviedb.feature.tvshows.ui.tv_show_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.elna.moviedb.core.model.TvShowDetails


class TvShowDetailsViewModel(
    private val tvShowId: Int,
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvShowDetailsUiState>(TvShowDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getTvShowDetails(tvShowId)
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

    fun retry() {
        getTvShowDetails(tvShowId)
    }

    sealed interface TvShowDetailsUiState {
        data object Loading : TvShowDetailsUiState
        data class Success(val tvShowDetails: TvShowDetails) : TvShowDetailsUiState
        data class Error(val message: String) : TvShowDetailsUiState
    }
}
