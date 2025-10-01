package com.elna.moviedb.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.data.search.SearchRepository
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.feature.search.model.SearchUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.ALL)

    private val searchResults = combine(
        _searchQuery.debounce(300).distinctUntilChanged(),
        _selectedFilter
    ) { query, filter -> query to filter }
        .flatMapLatest { (query, filter) ->
            if (query.isBlank()) {
                flow { emit(AppResult.Success(emptyList())) }
            } else {
                when (filter) {
                    SearchFilter.ALL -> searchRepository.searchAll(query)
                    SearchFilter.MOVIES -> searchRepository.searchMovies(query).map { result ->
                        when (result) {
                            is AppResult.Success -> AppResult.Success(result.data.map { it as SearchResultItem })
                            is AppResult.Error -> AppResult.Error(message = result.message, throwable = result.throwable)
                        }
                    }
                    SearchFilter.TV_SHOWS -> searchRepository.searchTvShows(query).map { result ->
                        when (result) {
                            is AppResult.Success -> AppResult.Success(result.data.map { it as SearchResultItem })
                            is AppResult.Error -> AppResult.Error(message = result.message, throwable = result.throwable)
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppResult.Success(emptyList())
        )

    val uiState: StateFlow<SearchUiState> = combine(
        searchResults,
        _searchQuery,
        _selectedFilter
    ) { result, query, filter ->
        SearchUiState(
            searchQuery = query,
            selectedFilter = filter,
            hasSearched = query.isNotBlank(),
            isLoading = false,
            searchResults = when (result) {
                is AppResult.Success -> result.data
                is AppResult.Error -> emptyList()
            },
            errorMessage = when (result) {
                is AppResult.Success -> null
                is AppResult.Error -> result.message
            },
            totalResults = when (result) {
                is AppResult.Success -> result.data.size
                is AppResult.Error -> 0
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChanged(filter: SearchFilter) {
        _selectedFilter.update { filter }
    }

    fun onRetry() {
        // Trigger a new search by resetting and setting the query again
        val currentQuery = _searchQuery.value
        _searchQuery.value = ""
        _searchQuery.value = currentQuery
    }
}