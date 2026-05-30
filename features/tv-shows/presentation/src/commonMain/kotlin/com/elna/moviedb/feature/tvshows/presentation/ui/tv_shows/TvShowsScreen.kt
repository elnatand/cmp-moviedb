package com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsEvent
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiAction
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowsUiState
import com.elna.moviedb.feature.tvshows.presentation.ui.components.TvShowsSection
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.network_error
import com.elna.moviedb.resources.on_the_air_tv_shows
import com.elna.moviedb.resources.popular_tv_shows
import com.elna.moviedb.resources.top_rated_tv_shows
import com.elna.moviedb.resources.tv_shows
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvShowsScreen(
    onClick: (id: Int, title: String, category: TvShowCategory) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val viewModel = koinViewModel<TvShowsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TvShowsScreen(
        uiState = uiState,
        onClick = onClick,
        onEvent = viewModel::onEvent,
        uiActions = viewModel.uiAction,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvShowsScreen(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String, category: TvShowCategory) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    uiActions: Flow<TvShowsUiAction>,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val paginationErrorMessage = stringResource(Res.string.network_error)

    // Handle UI actions (one-time events)
    LaunchedEffect(Unit) {
        uiActions.collect { effect ->
            when (effect) {
                is TvShowsUiAction.ShowPaginationError -> {
                    snackbarHostState.showSnackbar(
                        message = paginationErrorMessage,
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
                        onEvent = onEvent,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvShowsContent(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String, category: TvShowCategory) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onEvent(TvShowsEvent.Refresh) }
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
                val isFailed = uiState.hasFailed(category)

                // Show section if it has data, is loading (initial load), or failed (so the
                // inline error + retry is reachable instead of the section silently vanishing).
                if (tvShows.isNotEmpty() || isLoading || isFailed) {
                    TvShowsSection(
                        category = category,
                        title = stringResource(
                            getCategoryStringResource(
                                category
                            )
                        ),
                        tvShows = tvShows,
                        onClick = onClick,
                        isLoading = isLoading,
                        isFailed = isFailed,
                        onLoadMore = { onEvent(TvShowsEvent.LoadNextPage(category)) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
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
