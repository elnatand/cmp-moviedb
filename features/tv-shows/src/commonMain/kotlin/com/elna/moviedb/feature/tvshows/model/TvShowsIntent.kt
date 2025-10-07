package com.elna.moviedb.feature.tvshows.model

/**
 * Represents all possible user actions/intents in the TV Shows screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface TvShowsIntent {
    /**
     * User scrolled to the bottom and wants to load more TV shows
     */
    data object LoadNextPage : TvShowsIntent

    /**
     * User clicked retry button after an error
     */
    data object Retry : TvShowsIntent
}
