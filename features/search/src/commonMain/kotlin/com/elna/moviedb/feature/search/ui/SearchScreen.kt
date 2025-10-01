package com.elna.moviedb.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.SearchFilter
import com.elna.moviedb.core.model.SearchResultItem
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.search.model.SearchUiState
import com.elna.moviedb.feature.search.ui.components.SearchBar
import com.elna.moviedb.feature.search.ui.components.SearchEmptyState
import com.elna.moviedb.feature.search.ui.components.SearchFilters
import org.koin.compose.viewmodel.koinViewModel
import com.elna.moviedb.feature.search.ui.components.SearchResultItem as SearchResultItemComponent

@Composable
fun SearchScreen(
    onMovieClicked: (Int) -> Unit,
    onTvShowClicked: (Int) -> Unit,
) {
    val viewModel = koinViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFilterChanged = viewModel::onFilterChanged,
        onRetry = viewModel::onRetry,
        onMovieClicked = onMovieClicked,
        onTvShowClicked = onTvShowClicked
    )
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    onSearchQueryChanged: (String) -> Unit,
    onFilterChanged: (SearchFilter) -> Unit,
    onRetry: () -> Unit,
    onMovieClicked: (Int) -> Unit,
    onTvShowClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChanged = onSearchQueryChanged
            )

            SearchFilters(
                selectedFilter = uiState.selectedFilter,
                onFilterChanged = onFilterChanged
            )

            when {
                uiState.isLoading -> {
                    AppLoader(modifier = Modifier.fillMaxSize())
                }

                uiState.errorMessage != null -> {
                    AppErrorComponent(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                }

                uiState.searchResults.isEmpty() -> {
                    SearchEmptyState(
                        hasSearched = uiState.hasSearched,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.searchResults) { item ->
                            SearchResultItemComponent(
                                item = item,
                                onItemClicked = {
                                    when (item) {
                                        is SearchResultItem.MovieItem -> onMovieClicked(item.movie.id)
                                        is SearchResultItem.TvShowItem -> onTvShowClicked(item.tvShow.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}