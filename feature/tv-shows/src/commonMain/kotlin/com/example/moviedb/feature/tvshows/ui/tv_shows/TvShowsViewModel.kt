package com.example.moviedb.feature.tvshows.ui.tv_shows


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.moviedb.core.data.tv_shows.TvShowsRepository
import com.example.moviedb.core.model.TvShow
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val page = 1

    init {
        getTvShows()
    }

    private fun getTvShows() {
        viewModelScope.launch(Dispatchers.IO) {
            val tvShows = tvShowsRepository.getTvShowsPage(page)
            _uiState.update {
                it.copy(tvShows = tvShows)
            }
        }
    }

    data class UiState(
        val tvShows: List<TvShow> = emptyList()
    )
}