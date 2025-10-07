package com.elna.moviedb.feature.search.model

import com.elna.moviedb.core.model.SearchFilter

/**
 * Represents all possible user actions/intents in the Search screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface SearchIntent {
    /**
     * User changed the search query
     */
    data class UpdateSearchQuery(val query: String) : SearchIntent

    /**
     * User changed the search filter
     */
    data class UpdateFilter(val filter: SearchFilter) : SearchIntent

    /**
     * User scrolled to the bottom and wants to load more results
     */
    data object LoadMore : SearchIntent

    /**
     * User clicked retry button after an error
     */
    data object Retry : SearchIntent
}
