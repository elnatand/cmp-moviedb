package com.example.moviedb.feature.tvshows.model

import com.example.moviedb.core.model.TvShow

data class TvShowsUiState(
    val state: State,
    val tvShows: List<TvShow> = emptyList()
) {
    enum class State {
        LOADING, ERROR, SUCCESS
    }
}