package com.elna.moviedb.feature.tvshows.model

import com.elna.moviedb.core.model.TvShow

data class TvShowsUiState(
    val state: State,
    val tvShows: List<TvShow> = emptyList()
) {
    enum class State {
        LOADING, ERROR, SUCCESS
    }
}