package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.state == TvShowsUiState.State.ERROR && !uiState.hasAnyData -> {
                    AppErrorComponent(onRetry = { onEvent(TvShowsEvent.Retry) })
                }
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
        }
    }
}

@Composable
private fun TvShowsContent(
    uiState: TvShowsUiState,
    onClick: (id: Int, title: String, category: TvShowCategory) -> Unit,
    onEvent: (TvShowsEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        TvShowCategory.entries.forEach { category ->
            val tvShows = uiState.getTvShows(category)
            val isLoading = uiState.isLoading(category)

            if (tvShows.isNotEmpty() || isLoading) {
                TvShowsSection(
                    category = category,
                    title = stringResource(getCategoryStringResource(category)),
                    tvShows = tvShows,
                    onClick = onClick,
                    isLoading = isLoading,
                    onLoadMore = { onEvent(TvShowsEvent.LoadNextPage(category)) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun getCategoryStringResource(category: TvShowCategory) = when (category) {
    TvShowCategory.POPULAR -> Res.string.popular_tv_shows
    TvShowCategory.TOP_RATED -> Res.string.top_rated_tv_shows
    TvShowCategory.ON_THE_AIR -> Res.string.on_the_air_tv_shows
}
