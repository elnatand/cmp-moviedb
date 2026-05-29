package com.elna.moviedb.feature.tvshows.presentation.model

/**
 * Represents all possible user actions/events in the TV Show Details screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface TvShowDetailsEvent {
    /**
     * User clicked retry button after an error
     */
    data object Retry : TvShowDetailsEvent
}
