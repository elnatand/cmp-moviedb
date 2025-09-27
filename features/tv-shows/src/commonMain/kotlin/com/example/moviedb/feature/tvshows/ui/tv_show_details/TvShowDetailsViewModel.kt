package com.example.moviedb.feature.tvshows.ui.tv_show_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.core.data.tv_shows.TvShowsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.moviedb.core.model.TvShowDetails


class TvShowDetailsViewModel(
    private val tvShowId: Int,
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvShowDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTvShowDetails()
    }

    private fun getTvShowDetails() {
        viewModelScope.launch {
            val tvShowDetails = tvShowsRepository.getTvShowDetails(tvShowId)
            _uiState.update {
                it.copy(tvShowDetails = tvShowDetails)
            }
        }
    }

    data class TvShowDetailsUiState(
        val tvShowDetails: TvShowDetails? = null
    )
}