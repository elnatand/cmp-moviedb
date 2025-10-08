package com.elna.moviedb.feature.tvshows.model

/**
 * Represents all possible user actions/events in the TV Shows screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface TvShowsEvent {
    /**
     * User scrolled to the bottom and wants to load more TV shows
     */
    data object LoadNextPage : TvShowsEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : TvShowsEvent
}
