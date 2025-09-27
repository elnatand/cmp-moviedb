package com.example.moviedb.feature.tvshows.ui.tv_shows


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.moviedb.core.data.tv_shows.TvShowsRepository
import com.example.moviedb.core.model.TvShow
import com.example.moviedb.feature.tvshows.model.TvShowsUiState


class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvShowsUiState(TvShowsUiState.State.LOADING))
    val uiState = _uiState.asStateFlow()


    init {
        getTvShows()
    }

    private fun getTvShows() {
        viewModelScope.launch {
            val tvShows = tvShowsRepository.getTvShowsPage()
            _uiState.update {
                it.copy(tvShows = tvShows)
            }
        }
    }
}