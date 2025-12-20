package com.elna.moviedb.feature.tvshows.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowTile

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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowsSection(
    title: String,
    tvShows: List<TvShow>,
    onClick: (id: Int, title: String) -> Unit,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
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
                    onClick = onClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }

            // Show loader when loading (for both initial load and pagination)
            if (isLoading) {
                item(key = "loader") {
                    Box(
                        modifier = Modifier
                            .height(240.dp)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppLoader()
                    }
                }
            }
        }
    }
}