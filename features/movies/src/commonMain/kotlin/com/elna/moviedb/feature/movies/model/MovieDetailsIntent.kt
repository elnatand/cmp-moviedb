package com.elna.moviedb.feature.movies.model

/**
 * Represents all possible user actions/intents in the Movie Details screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface MovieDetailsIntent {
    /**
     * User clicked retry button after an error
     */
    data object Retry : MovieDetailsIntent
}
