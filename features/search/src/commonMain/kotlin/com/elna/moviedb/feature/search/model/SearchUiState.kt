package com.elna.moviedb.feature.search.model

import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<SearchResultItem> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val totalResults: Int = 0,
    val hasSearched: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
)