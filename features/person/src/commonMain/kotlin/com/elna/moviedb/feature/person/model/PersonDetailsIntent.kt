package com.elna.moviedb.feature.person.model

/**
 * Represents all possible user actions/intents in the Person Details screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface PersonDetailsIntent {
    /**
     * User clicked retry button after an error
     */
    data object Retry : PersonDetailsIntent
}
