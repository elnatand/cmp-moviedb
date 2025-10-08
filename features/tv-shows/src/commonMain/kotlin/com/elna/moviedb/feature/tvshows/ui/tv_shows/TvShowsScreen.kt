package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.tvshows.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.network_error
import com.elna.moviedb.resources.popular_tv_shows
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
                title = { Text(text = stringResource(Res.string.popular_tv_shows)) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Show in-memory data if available
                uiState.tvShows.isNotEmpty() -> {
                    TvShowsList(
                        uiState = uiState,
                        onClick = onClick,
                        onLoadMore = { onEvent(TvShowsEvent.LoadNextPage) }
                    )
                }

                // Show error screen (only triggered when repository emits error)
                uiState.state == TvShowsUiState.State.ERROR -> {
                    AppErrorComponent(
                        message = stringResource(Res.string.network_error),
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
private fun TvShowsList(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String) -> Unit,
    onLoadMore: () -> Unit
) {
    val gridState = rememberLazyGridState()

    // Detect when user reaches the bottom
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            (lastVisibleItemIndex + 6 > totalItemsNumber) && uiState.state != TvShowsUiState.State.LOADING
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
        content = {
            items(uiState.tvShows) {
                TvShowTile(
                    tvShow = it,
                    onClick = onClick
                )
            }
        }
    )
}