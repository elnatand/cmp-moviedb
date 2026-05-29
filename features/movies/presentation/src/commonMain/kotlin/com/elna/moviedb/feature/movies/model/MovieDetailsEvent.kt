package com.elna.moviedb.feature.movies.model

/**
 * Represents all possible user actions/events in the Movie Details screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface MovieDetailsEvent {
    /**
     * User clicked retry button after an error
     */
    data object Retry : MovieDetailsEvent
}
