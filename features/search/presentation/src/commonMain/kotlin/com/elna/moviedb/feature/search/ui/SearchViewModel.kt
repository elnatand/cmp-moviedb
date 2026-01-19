package com.elna.moviedb.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.domain.repository.SearchRepository
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.model.SearchEvent
import com.elna.moviedb.feature.search.model.SearchUiState
import com.elna.moviedb.feature.search.strategy.AllSearchStrategy
import com.elna.moviedb.feature.search.strategy.MovieSearchStrategy
import com.elna.moviedb.feature.search.strategy.PeopleSearchStrategy
import com.elna.moviedb.feature.search.strategy.SearchStrategy
import com.elna.moviedb.feature.search.strategy.TvShowSearchStrategy
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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
 * - Intent: [SearchEvent] - User actions/intentions
 *
 */
@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private var currentSearchJob: Job? = null

    private val strategies by lazy {
        mapOf(
            SearchFilter.ALL to AllSearchStrategy(searchRepository),
            SearchFilter.MOVIES to MovieSearchStrategy(searchRepository),
            SearchFilter.TV_SHOWS to TvShowSearchStrategy(searchRepository),
            SearchFilter.PEOPLE to PeopleSearchStrategy(searchRepository),
        )
    }

    private fun getStrategyForFilter(filter: SearchFilter): SearchStrategy =
        strategies.getValue(filter)

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
    fun onEvent(intent: SearchEvent) {
        when (intent) {
            is SearchEvent.UpdateSearchQuery -> onSearchQueryChanged(intent.query)
            is SearchEvent.UpdateFilter -> onFilterChanged(intent.filter)
            SearchEvent.LoadMore -> onLoadMore()
            SearchEvent.Retry -> onRetry()
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
     * Performs a search using the Strategy Pattern.
     * This method is now closed for modification - new filters can be added by:
     * 1. Creating a new SearchStrategy implementation
     * 2. Adding a case to getStrategyForFilter()
     *
     * No changes to this method are needed when adding new filter types.
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
                    errorMessage = null
                )
            }

            // Strategy Pattern - delegate search to the appropriate strategy
            val strategy = getStrategyForFilter(filter)
            val result = strategy.search(query, page).first()

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