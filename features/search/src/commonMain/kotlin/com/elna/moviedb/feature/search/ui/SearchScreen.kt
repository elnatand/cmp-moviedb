package com.elna.moviedb.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
    onPersonClicked: (Int) -> Unit,
) {
    val viewModel = koinViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onSearchQueryChanged = { viewModel.onEvent(com.elna.moviedb.feature.search.model.SearchEvent.UpdateSearchQuery(it)) },
        onFilterChanged = { viewModel.onEvent(com.elna.moviedb.feature.search.model.SearchEvent.UpdateFilter(it)) },
        onRetry = { viewModel.onEvent(com.elna.moviedb.feature.search.model.SearchEvent.Retry) },
        onLoadMore = { viewModel.onEvent(com.elna.moviedb.feature.search.model.SearchEvent.LoadMore) },
        onMovieClicked = onMovieClicked,
        onTvShowClicked = onTvShowClicked,
        onPersonClicked = onPersonClicked
    )
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    onSearchQueryChanged: (String) -> Unit,
    onFilterChanged: (SearchFilter) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onMovieClicked: (Int) -> Unit,
    onTvShowClicked: (Int) -> Unit,
    onPersonClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
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
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = modifier.fillMaxSize()
                ) {
                    AppLoader(modifier.padding(top = 100.dp))
                }
            }

            uiState.errorMessage != null -> {
                AppErrorComponent(
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
                val listState = rememberLazyListState()

                val shouldLoadMore by remember {
                    derivedStateOf {
                        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                        val totalItems = listState.layoutInfo.totalItemsCount
                        lastVisibleItem?.index == totalItems - 6 && totalItems > 0
                    }
                }

                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore && uiState.hasMorePages && !uiState.isLoadingMore) {
                        onLoadMore()
                    }
                }

                LazyColumn(
                    state = listState,
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
                                    is SearchResultItem.PersonItem -> onPersonClicked(item.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}