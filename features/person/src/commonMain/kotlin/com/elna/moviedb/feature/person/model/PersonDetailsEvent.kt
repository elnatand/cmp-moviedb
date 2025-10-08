package com.elna.moviedb.feature.person.model

/**
 * Represents all possible user actions/events in the Person Details screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface PersonDetailsEvent {
    /**
     * User clicked retry button after an error
     */
    data object Retry : PersonDetailsEvent
}
