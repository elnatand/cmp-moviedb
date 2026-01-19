package com.elna.moviedb.feature.search.presentation.model

import com.elna.moviedb.feature.search.domain.model.SearchFilter

/**
 * Represents all possible user actions/events in the Search screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface SearchEvent {
    /**
     * User changed the search query
     */
    data class UpdateSearchQuery(val query: String) : SearchEvent

    /**
     * User changed the search filter
     */
    data class UpdateFilter(val filter: SearchFilter) : SearchEvent

    /**
     * User scrolled to the bottom and wants to load more results
     */
    data object LoadMore : SearchEvent

    /**
     * User clicked retry button after an error
     */
    data object Retry : SearchEvent
}
