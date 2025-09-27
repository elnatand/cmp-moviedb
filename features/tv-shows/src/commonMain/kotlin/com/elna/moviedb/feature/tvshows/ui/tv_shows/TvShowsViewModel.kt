package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState

class TvShowsViewModel(
    private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvShowsUiState(state = TvShowsUiState.State.LOADING))
    val uiState = _uiState.asStateFlow()

    init {
        observeTvShows()
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

    fun loadNextPage() {
        _uiState.update { state ->
            state.copy(state = TvShowsUiState.State.LOADING)
        }

        viewModelScope.launch {
            tvShowsRepository.loadNextPage()
        }
    }
}