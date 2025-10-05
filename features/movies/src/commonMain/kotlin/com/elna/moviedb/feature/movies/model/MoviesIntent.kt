package com.elna.moviedb.feature.movies.model

/**
 * Represents all possible user actions/intents in the Movies screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface MoviesIntent {
    /**
     * User scrolled to the bottom and wants to load more movies
     */
    data object LoadNextPage : MoviesIntent

    /**
     * User clicked retry button after an error
     */
    data object Retry : MoviesIntent
}
