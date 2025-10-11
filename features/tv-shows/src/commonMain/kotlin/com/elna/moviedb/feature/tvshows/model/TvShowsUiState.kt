package com.elna.moviedb.feature.tvshows.model

import com.elna.moviedb.core.model.TvShow

data class TvShowsUiState(
    val state: State,
    val popularTvShows: List<TvShow> = emptyList(),
    val topRatedTvShows: List<TvShow> = emptyList(),
    val onTheAirTvShows: List<TvShow> = emptyList(),
    val isLoadingPopular: Boolean = false,
    val isLoadingTopRated: Boolean = false,
    val isLoadingOnTheAir: Boolean = false
) {
    enum class State {
        LOADING, ERROR, SUCCESS
    }
}