package com.elna.moviedb.feature.search.presentation.model

import androidx.compose.runtime.Immutable
import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem

/**
 * Marked [Immutable] because [searchResults] is a read-only [List] Compose otherwise treats
 * as unstable, making the search screen non-skippable. The contract holds: the list is only
 * ever replaced via copy(), never mutated in place.
 */
@Immutable
data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<SearchResultItem> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: DataError? = null,
    val hasSearched: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
)