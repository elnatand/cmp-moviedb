package com.elna.moviedb.feature.tvshows.model

/**
 * Represents all possible user actions/events in the TV Shows screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface TvShowsEvent {
    /**
     * User scrolled near the end of a TV show category and wants to load more.
     *
     * @property category The TV show category to load the next page for
     */
    data class LoadNextPage(val category: TvShowCategory) : TvShowsEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : TvShowsEvent
}
