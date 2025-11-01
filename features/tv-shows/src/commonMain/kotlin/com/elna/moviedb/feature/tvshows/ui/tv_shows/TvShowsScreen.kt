package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.feature.tvshows.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.model.TvShowsUiState
import com.elna.moviedb.feature.tvshows.ui.components.TvShowsSection
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
                // Show error screen only when there's no data and repository emits error
                uiState.state == TvShowsUiState.State.ERROR && !uiState.hasAnyData -> {
                    AppErrorComponent(
                        onRetry = { onEvent(TvShowsEvent.Retry) }
                    )
                }

                // Show content with section-specific loaders
                else -> {
                    TvShowsContent(
                        uiState = uiState,
                        onClick = onClick,
                        onEvent = onEvent
                    )
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
            val isLoading = uiState.isLoading(category)

            // Show section if it has data OR if it's loading (initial load)
            if (tvShows.isNotEmpty() || isLoading) {
                TvShowsSection(
                    title = stringResource(getCategoryStringResource(category)),
                    tvShows = tvShows,
                    onClick = onClick,
                    isLoading = isLoading,
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
