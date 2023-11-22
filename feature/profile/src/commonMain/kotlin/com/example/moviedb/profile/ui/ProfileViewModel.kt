package com.example.moviedb.profile.ui


import com.example.moviedb.model.TvShow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.viewmodel.ViewModel

class ProfileViewModel(
  //  private val tvShowsRepository: TvShowsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()



    data class UiState(
        val tvShows: List<TvShow> = emptyList()
    )
}