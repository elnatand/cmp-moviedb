package com.elna.moviedb.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.feature.search.model.SearchIntent
import com.elna.moviedb.feature.search.model.SearchUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for Search screen.
 *
 * MVI Components:
 * - Model: [SearchUiState] - Immutable state representing the UI
 * - View: SearchScreen - Renders the state and dispatches intents
 * - Intent: [SearchIntent] - User actions/intentions
 */
@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState
                .debounce(300)
                .distinctUntilChanged { old, new ->
                    old.searchQuery == new.searchQuery && old.selectedFilter == new.selectedFilter
                }
                .collect { state ->
                    if (state.searchQuery.isNotBlank()) {
                        performSearch(state.searchQuery, state.selectedFilter, 1)
                    }
                }
        }
    }

    /**
     * Main entry point for handling user intents.
     * All UI interactions should go through this method.
     */
    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateSearchQuery -> onSearchQueryChanged(intent.query)
            is SearchIntent.UpdateFilter -> onFilterChanged(intent.filter)
            SearchIntent.LoadMore -> onLoadMore()
            SearchIntent.Retry -> onRetry()
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = emptyList(),
                currentPage = 1,
                hasMorePages = true,
                errorMessage = null
            )
        }
    }

    private fun onFilterChanged(filter: SearchFilter) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                searchResults = emptyList(),
                currentPage = 1,
                hasMorePages = true,
                errorMessage = null
            )
        }
    }

    private fun onLoadMore() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMorePages || currentState.searchQuery.isBlank()) {
            return
        }

        val nextPage = currentState.currentPage + 1
        performSearch(currentState.searchQuery, currentState.selectedFilter, nextPage, isLoadingMore = true)
    }

    private fun onRetry() {
        val currentState = _uiState.value
        if (currentState.searchQuery.isNotBlank()) {
            performSearch(currentState.searchQuery, currentState.selectedFilter, 1)
        }
    }

    private fun performSearch(
        query: String,
        filter: SearchFilter,
        page: Int,
        isLoadingMore: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isLoadingMore,
                    isLoadingMore = isLoadingMore,
                    hasSearched = true,
                    errorMessage = null
                )
            }

            val result = when (filter) {
                SearchFilter.ALL -> searchRepository.searchAll(query, page).first()

                SearchFilter.MOVIES -> {
                    when (val movieResult = searchRepository.searchMovies(query, page).first()) {
                        is AppResult.Success -> AppResult.Success(movieResult.data.map { it as SearchResultItem })
                        is AppResult.Error -> movieResult
                    }
                }

                SearchFilter.TV_SHOWS -> {
                    when (val tvShowResult = searchRepository.searchTvShows(query, page).first()) {
                        is AppResult.Success -> AppResult.Success(tvShowResult.data.map { it as SearchResultItem })
                        is AppResult.Error -> tvShowResult
                    }
                }

                SearchFilter.PEOPLE -> {
                    when (val peopleResult = searchRepository.searchPeople(query, page).first()) {
                        is AppResult.Success -> AppResult.Success(peopleResult.data.map { it as SearchResultItem })
                        is AppResult.Error -> peopleResult
                    }
                }
            }

            _uiState.update { currentState ->
                when (result) {
                    is AppResult.Success -> {
                        val newResults = if (isLoadingMore) {
                            currentState.searchResults + result.data
                        } else {
                            result.data
                        }
                        currentState.copy(
                            searchResults = newResults,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = page,
                            hasMorePages = result.data.isNotEmpty(),
                            totalResults = newResults.size,
                            errorMessage = null
                        )
                    }
                    is AppResult.Error -> {
                        currentState.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}