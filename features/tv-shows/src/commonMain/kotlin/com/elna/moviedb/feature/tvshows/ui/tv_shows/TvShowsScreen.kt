package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.tvshows.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.on_the_air_tv_shows
import com.elna.moviedb.resources.popular_tv_shows
import com.elna.moviedb.resources.top_rated_tv_shows
import com.elna.moviedb.resources.tv_shows
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvShowsScreen(
    onClick: (id: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel<TvShowsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TvShowsScreen(
        uiState = uiState,
        onClick = onClick,
        onEvent = viewModel::onEvent,
        uiActions = viewModel.uiAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvShowsScreen(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    uiActions: Flow<TvShowsUiAction>
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle UI actions (one-time events)
    LaunchedEffect(Unit) {
        uiActions.collect { effect ->
            when (effect) {
                is TvShowsUiAction.ShowPaginationError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        withDismissAction = true
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.tv_shows)) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Show in-memory data if available
                uiState.hasAnyData -> {
                    TvShowsContent(
                        uiState = uiState,
                        onClick = onClick,
                        onEvent = onEvent
                    )
                }

                // Show error screen (only triggered when repository emits error)
                uiState.state == TvShowsUiState.State.ERROR -> {
                    AppErrorComponent(
                        onRetry = { onEvent(TvShowsEvent.Retry) }
                    )
                }

                // Show loader during initial loading (repository hasn't emitted yet)
                uiState.state == TvShowsUiState.State.LOADING -> {
                    AppLoader()
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun TvShowsContent(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String) -> Unit,
    onEvent: (TvShowsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        // Dynamically render all TV show categories
        // Following OCP - adding new categories requires ZERO changes here
        TvShowCategory.entries.forEach { category ->
            val tvShows = uiState.getTvShows(category)
            if (tvShows.isNotEmpty()) {
                TvShowsSection(
                    title = stringResource(getCategoryStringResource(category)),
                    tvShows = tvShows,
                    onClick = onClick,
                    isLoading = uiState.isLoading(category),
                    onLoadMore = { onEvent(TvShowsEvent.LoadNextPage(category)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(70.dp))
    }
}

/**
 * Maps TvShowCategory to its corresponding string resource.
 * This is the only place that needs updating when adding a new category.
 */
@Composable
private fun getCategoryStringResource(category: TvShowCategory) = when (category) {
    TvShowCategory.POPULAR -> Res.string.popular_tv_shows
    TvShowCategory.TOP_RATED -> Res.string.top_rated_tv_shows
    TvShowCategory.ON_THE_AIR -> Res.string.on_the_air_tv_shows
}

/**
 * Displays a horizontal scrolling section of TV shows with automatic pagination.
 * Monitors scroll position and triggers loading of more content when user scrolls near the end.
 *
 * @param title Section title to display
 * @param tvShows List of TV shows to display
 * @param onClick Callback when a TV show is clicked, receives (id, title)
 * @param isLoading Whether pagination is currently loading
 * @param onLoadMore Callback to trigger loading more TV shows
 */
@Composable
private fun TvShowsSection(
    title: String,
    tvShows: List<TvShow>,
    onClick: (id: Int, title: String) -> Unit,
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()
    val currentIsLoading by rememberUpdatedState(isLoading)

    // Automatic pagination: Detect when user scrolls near the end to trigger loading more
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Trigger pagination when user is 3 items away from the end
            lastVisibleItemIndex >= totalItemsNumber - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && !currentIsLoading) {
                onLoadMore()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = tvShows,
                key = { it.id }
            ) { tvShow ->
                TvShowTile(
                    tvShow = tvShow,
                    onClick = onClick
                )
            }
        }
    }
}