package com.elna.moviedb.feature.tvshows.model

import com.elna.moviedb.core.model.TvShow

/**
 * Represents the UI state for the TV Shows screen.
 * Contains three separate lists of TV shows and their individual pagination states.
 *
 * @property state Overall screen state (LOADING, ERROR, SUCCESS)
 * @property popularTvShows List of popular TV shows
 * @property topRatedTvShows List of top-rated TV shows
 * @property onTheAirTvShows List of currently airing TV shows
 * @property isLoadingPopular True when loading more popular TV shows (pagination)
 * @property isLoadingTopRated True when loading more top-rated TV shows (pagination)
 * @property isLoadingOnTheAir True when loading more on-the-air TV shows (pagination)
 */
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