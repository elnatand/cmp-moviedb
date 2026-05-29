package com.elna.moviedb.feature.search.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.domain.repository.SearchRepository
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.presentation.model.SearchEvent
import com.elna.moviedb.feature.search.presentation.model.SearchUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for Search screen.
 *
 * MVI Components:
 * - Model: [SearchUiState] - Immutable state representing the UI
 * - View: SearchScreen - Renders the state and dispatches intents
 * - Intent: [SearchEvent] - User actions/intentions
 *
 */
@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var currentSearchJob: Job? = null

    /**
     * The (query, filter) pair that drives automatic searching. Kept separate from
     * [_uiState] so the debounce reacts only to user intent — not to incidental state
     * updates like loading toggles or incoming results (which would otherwise restart the
     * debounce window and could delay or drop searches).
     */
    private data class SearchTrigger(val query: String, val filter: SearchFilter)

    private val searchTrigger = MutableStateFlow(SearchTrigger("", SearchFilter.ALL))

    init {
        viewModelScope.launch {
            searchTrigger
                .debounce(300)
                .distinctUntilChanged()
                .collect { trigger ->
                    if (trigger.query.isNotBlank()) {
                        performSearch(trigger.query, trigger.filter, 1)
                    }
                }
        }
    }

    /**
     * Main entry point for handling user intents.
     * All UI interactions should go through this method.
     */
    fun onEvent(intent: SearchEvent) {
        when (intent) {
            is SearchEvent.UpdateSearchQuery -> onSearchQueryChanged(intent.query)
            is SearchEvent.UpdateFilter -> onFilterChanged(intent.filter)
            SearchEvent.LoadMore -> onLoadMore()
            SearchEvent.Retry -> onRetry()
        }
    }

    private fun onSearchQueryChanged(query: String) {
        // Cancel any in-flight search so its (now stale) results can't land after the query
        // changed — e.g. clearing the field while a request for the previous query is running.
        // A fresh search is re-issued by the debounced trigger below.
        currentSearchJob?.cancel()
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = emptyList(),
                currentPage = 1,
                hasMorePages = true,
                error = null
            )
        }
        searchTrigger.update { it.copy(query = query) }
    }

    private fun onFilterChanged(filter: SearchFilter) {
        // Same rationale as onSearchQueryChanged: drop the in-flight result for the old filter.
        currentSearchJob?.cancel()
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                searchResults = emptyList(),
                currentPage = 1,
                hasMorePages = true,
                error = null
            )
        }
        searchTrigger.update { it.copy(filter = filter) }
    }

    private fun onLoadMore() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMorePages || currentState.searchQuery.isBlank()) {
            return
        }

        val nextPage = currentState.currentPage + 1
        performSearch(
            currentState.searchQuery,
            currentState.selectedFilter,
            nextPage,
            isLoadingMore = true
        )
    }

    private fun onRetry() {
        val currentState = _uiState.value
        if (currentState.searchQuery.isNotBlank()) {
            performSearch(currentState.searchQuery, currentState.selectedFilter, 1)
        }
    }

    /**
     * Performs a search for the given filter.
     *
     * The filter is just a parameter forwarded to the repository, which selects the
     * right TMDB endpoint and mapping. Adding a new filter requires no changes here.
     */
    private fun performSearch(
        query: String,
        filter: SearchFilter,
        page: Int,
        isLoadingMore: Boolean = false
    ) {
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isLoadingMore,
                    isLoadingMore = isLoadingMore,
                    hasSearched = true,
                    error = null
                )
            }

            val result = searchRepository.search(filter, query, page)

            _uiState.update { currentState ->
                when (result) {
                    is AppResult.Success -> {
                        val searchPage = result.data
                        val newResults = if (isLoadingMore) {
                            currentState.searchResults + searchPage.items
                        } else {
                            searchPage.items
                        }
                        currentState.copy(
                            searchResults = newResults,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = searchPage.page,
                            hasMorePages = searchPage.hasMorePages,
                            error = null
                        )
                    }

                    is AppResult.Error -> {
                        currentState.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = result.type
                        )
                    }
                }
            }
        }
    }
}