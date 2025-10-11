package com.elna.moviedb.feature.movies.ui.movies


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
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesUiAction
import com.elna.moviedb.feature.movies.model.MoviesUiState
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.network_error
import com.elna.moviedb.resources.popular_movies
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MoviesScreen(
    onClick: (movieId: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel<MoviesViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesScreen(
        uiState = uiState,
        onClick = onClick,
        onEvent = viewModel::onEvent,
        uiActions = viewModel.uiAction
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesScreen(
    uiState: MoviesUiState,
    onClick: (Int, String) -> Unit,
    onEvent: (MoviesEvent) -> Unit,
    uiActions: Flow<MoviesUiAction>
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle UI actions (one-time events)
    LaunchedEffect(Unit) {
        uiActions.collect { effect ->
            when (effect) {
                is MoviesUiAction.ShowPaginationError -> {
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
                title = { Text(text = stringResource(Res.string.popular_movies)) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Show cached data if available (offline-first)
                uiState.movies.isNotEmpty() -> {
                    MoviesList(
                        uiState = uiState,
                        onClick = onClick,
                        onLoadMore = { onEvent(MoviesEvent.LoadNextPage) }
                    )
                }

                // Show error screen only when cache is empty and initial load failed
                uiState.state == MoviesUiState.State.ERROR -> {
                    AppErrorComponent(
                        onRetry = { onEvent(MoviesEvent.Retry) }
                    )
                }

                // Show loader when initially loading (no cached data yet)
                uiState.state == MoviesUiState.State.LOADING -> {
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
private fun MoviesList(
    uiState: MoviesUiState,
    onClick: (Int, String) -> Unit,
    onLoadMore: () -> Unit
) {

    val gridState = rememberLazyGridState()

    // Detect when user reaches the bottom
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            (lastVisibleItemIndex + 6 > totalItemsNumber) && uiState.state != MoviesUiState.State.LOADING
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
            items(uiState.movies) {
                MovieTile(
                    movie = it,
                    onClick = onClick
                )
            }
        }
    )
}
