package com.example.moviedb.features.tv_shows.ui.tv_show_details

import com.example.moviedb.data.tv_shows.TvShowsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.moviedb.model.TvShowDetails
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TvShowDetailsViewModel(
    private val tvShowId: Int,
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTvShowDetails()
    }

    private fun getTvShowDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val tvShowDetails = tvShowsRepository.getTvShowDetails(tvShowId)
            _uiState.update {
                it.copy(tvShowDetails = tvShowDetails)
            }
        }
    }

    data class UiState(
        val tvShowDetails: TvShowDetails? = null
    )
}