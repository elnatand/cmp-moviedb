package com.elna.moviedb.feature.movies.model

/**
 * Represents all possible user actions/events in the Movies screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface MoviesEvent {
    /**
     * User scrolled to the bottom and wants to load more movies
     */
    data object LoadNextPage : MoviesEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : MoviesEvent
}
