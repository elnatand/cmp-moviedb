package com.elna.moviedb.feature.tvshows.model

/**
 * Represents all possible user actions/intents in the TV Show Details screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface TvShowDetailsIntent {
    /**
     * User clicked retry button after an error
     */
    data object Retry : TvShowDetailsIntent
}
