package com.elna.moviedb.feature.tvshows.model

/**
 * Represents all possible user actions/events in the TV Shows screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface TvShowsEvent {
    /**
     * User scrolled to the end of popular TV shows list
     */
    data object LoadNextPagePopular : TvShowsEvent

    /**
     * User scrolled to the end of top-rated TV shows list
     */
    data object LoadNextPageTopRated : TvShowsEvent

    /**
     * User scrolled to the end of on-the-air TV shows list
     */
    data object LoadNextPageOnTheAir : TvShowsEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : TvShowsEvent
}
