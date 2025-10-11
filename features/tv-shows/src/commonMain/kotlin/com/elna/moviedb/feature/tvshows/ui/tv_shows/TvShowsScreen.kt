package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
                uiState.popularTvShows.isNotEmpty() ||
                uiState.topRatedTvShows.isNotEmpty() ||
                uiState.onTheAirTvShows.isNotEmpty() -> {
                    TvShowsContent(
                        uiState = uiState,
                        onClick = onClick
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
    onClick: (id: Int, title: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        // Popular TV Shows Section
        if (uiState.popularTvShows.isNotEmpty()) {
            TvShowsSection(
                title = stringResource(Res.string.popular_tv_shows),
                tvShows = uiState.popularTvShows,
                onClick = onClick
            )
        }

        // Top Rated TV Shows Section
        if (uiState.topRatedTvShows.isNotEmpty()) {
            TvShowsSection(
                title = stringResource(Res.string.top_rated_tv_shows),
                tvShows = uiState.topRatedTvShows,
                onClick = onClick
            )
        }

        // On The Air TV Shows Section
        if (uiState.onTheAirTvShows.isNotEmpty()) {
            TvShowsSection(
                title = stringResource(Res.string.on_the_air_tv_shows),
                tvShows = uiState.onTheAirTvShows,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun TvShowsSection(
    title: String,
    tvShows: List<com.elna.moviedb.core.model.TvShow>,
    onClick: (id: Int, title: String) -> Unit
) {
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(tvShows) { tvShow ->
                TvShowTile(
                    tvShow = tvShow,
                    onClick = onClick
                )
            }
        }
    }
}