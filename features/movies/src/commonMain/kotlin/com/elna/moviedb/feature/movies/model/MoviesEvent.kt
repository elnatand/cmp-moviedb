package com.elna.moviedb.feature.movies.model

import com.elna.moviedb.core.model.MovieCategory

/**
 * Represents all possible user actions/events in the Movies screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 *
 * This follows the Open/Closed Principle - new movie categories can be added
 * to [MovieCategory] enum without modifying this sealed interface.
 */
sealed interface MoviesEvent {
    /**
     * User scrolled near the end of a movie category and wants to load more.
     *
     * @property category The movie category to load the next page for
     */
    data class LoadNextPage(val category: MovieCategory) : MoviesEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : MoviesEvent
}
