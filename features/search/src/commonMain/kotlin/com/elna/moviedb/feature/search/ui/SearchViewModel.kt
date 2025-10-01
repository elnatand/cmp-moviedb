package com.elna.moviedb.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.feature.search.model.SearchUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun onSearchQueryChanged(query: String) {
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

    fun onFilterChanged(filter: SearchFilter) {
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

    fun onLoadMore() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMorePages || currentState.searchQuery.isBlank()) {
            return
        }

        val nextPage = currentState.currentPage + 1
        performSearch(currentState.searchQuery, currentState.selectedFilter, nextPage, isLoadingMore = true)
    }

    fun onRetry() {
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
                SearchFilter.ALL -> {
                    var flowResult: AppResult<List<SearchResultItem>>? = null
                    searchRepository.searchAll(query, page).collect { flowResult = it }
                    flowResult
                }
                SearchFilter.MOVIES -> {
                    var flowResult: AppResult<List<SearchResultItem.MovieItem>>? = null
                    searchRepository.searchMovies(query, page).collect { flowResult = it }
                    flowResult?.let {
                        when (it) {
                            is AppResult.Success -> AppResult.Success(it.data.map { item -> item as SearchResultItem })
                            is AppResult.Error -> AppResult.Error(message = it.message, throwable = it.throwable)
                        }
                    }
                }
                SearchFilter.TV_SHOWS -> {
                    var flowResult: AppResult<List<SearchResultItem.TvShowItem>>? = null
                    searchRepository.searchTvShows(query, page).collect { flowResult = it }
                    flowResult?.let {
                        when (it) {
                            is AppResult.Success -> AppResult.Success(it.data.map { item -> item as SearchResultItem })
                            is AppResult.Error -> AppResult.Error(message = it.message, throwable = it.throwable)
                        }
                    }
                }
                SearchFilter.PEOPLE -> {
                    var flowResult: AppResult<List<SearchResultItem.PersonItem>>? = null
                    searchRepository.searchPeople(query, page).collect { flowResult = it }
                    flowResult?.let {
                        when (it) {
                            is AppResult.Success -> AppResult.Success(it.data.map { item -> item as SearchResultItem })
                            is AppResult.Error -> AppResult.Error(message = it.message, throwable = it.throwable)
                        }
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
                    null -> currentState.copy(
                        isLoading = false,
                        isLoadingMore = false
                    )
                }
            }
        }
    }
}