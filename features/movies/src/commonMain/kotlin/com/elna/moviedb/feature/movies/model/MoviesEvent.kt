package com.elna.moviedb.feature.movies.model

/**
 * Represents all possible user actions/events in the Movies screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface MoviesEvent {
    /**
     * User scrolled near the end of popular movies and wants to load more
     */
    data object LoadNextPagePopular : MoviesEvent

    /**
     * User scrolled near the end of top rated movies and wants to load more
     */
    data object LoadNextPageTopRated : MoviesEvent

    /**
     * User scrolled near the end of now playing movies and wants to load more
     */
    data object LoadNextPageNowPlaying : MoviesEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : MoviesEvent
}
