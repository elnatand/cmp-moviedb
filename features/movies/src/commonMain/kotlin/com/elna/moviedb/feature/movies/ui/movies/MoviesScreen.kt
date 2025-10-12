package com.elna.moviedb.feature.movies.ui.movies


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
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesUiAction
import com.elna.moviedb.feature.movies.model.MoviesUiState
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.movies
import com.elna.moviedb.resources.now_playing_movies
import com.elna.moviedb.resources.popular_movies
import com.elna.moviedb.resources.top_rated_movies
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
                title = { Text(text = stringResource(Res.string.movies)) },
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
                    MoviesContent(
                        uiState = uiState,
                        onClick = onClick,
                        onEvent = onEvent
                    )
                }

                // Show error screen (only triggered when repository emits error)
                uiState.state == MoviesUiState.State.ERROR -> {
                    AppErrorComponent(
                        onRetry = { onEvent(MoviesEvent.Retry) }
                    )
                }

                // Show loader during initial loading (repository hasn't emitted yet)
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
private fun MoviesContent(
    uiState: MoviesUiState,
    onClick: (id: Int, title: String) -> Unit,
    onEvent: (MoviesEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        // Popular Movies Section
        if (uiState.popularMovies.isNotEmpty()) {
            MoviesSection(
                title = stringResource(Res.string.popular_movies),
                movies = uiState.popularMovies,
                onClick = onClick,
                isLoading = uiState.isLoadingPopular,
                onLoadMore = { onEvent(MoviesEvent.LoadNextPagePopular) }
            )
        }

        // Top Rated Movies Section
        if (uiState.topRatedMovies.isNotEmpty()) {
            MoviesSection(
                title = stringResource(Res.string.top_rated_movies),
                movies = uiState.topRatedMovies,
                onClick = onClick,
                isLoading = uiState.isLoadingTopRated,
                onLoadMore = { onEvent(MoviesEvent.LoadNextPageTopRated) }
            )
        }

        // Now Playing Movies Section
        if (uiState.nowPlayingMovies.isNotEmpty()) {
            MoviesSection(
                title = stringResource(Res.string.now_playing_movies),
                movies = uiState.nowPlayingMovies,
                onClick = onClick,
                isLoading = uiState.isLoadingNowPlaying,
                onLoadMore = { onEvent(MoviesEvent.LoadNextPageNowPlaying) }
            )
        }
        Spacer(modifier = Modifier.height(70.dp))
    }
}

/**
 * Displays a horizontal scrolling section of movies with automatic pagination.
 * Monitors scroll position and triggers loading of more content when user scrolls near the end.
 *
 * @param title Section title to display
 * @param movies List of movies to display
 * @param onClick Callback when a movie is clicked, receives (id, title)
 * @param isLoading Whether pagination is currently loading
 * @param onLoadMore Callback to trigger loading more movies
 */
@Composable
private fun MoviesSection(
    title: String,
    movies: List<Movie>,
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
                items = movies,
                key = { it.id }
            ) { movie ->
                MovieTile(
                    movie = movie,
                    onClick = onClick
                )
            }
        }
    }
}
