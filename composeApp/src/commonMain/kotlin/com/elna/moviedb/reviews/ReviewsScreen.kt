package com.elna.moviedb.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.model.Review
import com.elna.moviedb.core.ui.design_system.AppBackButton
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.core.ui.utils.formatIso8601Date
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.all_reviews
import com.elna.moviedb.resources.no_reviews
import com.elna.moviedb.resources.read_more
import com.elna.moviedb.resources.show_less
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ReviewsScreen(
    contentId: Int,
    isMovie: Boolean,
    onBack: () -> Unit
) {
    val viewModel = koinViewModel<ReviewsViewModel> { parametersOf(contentId, isMovie) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewsScreen(
        uiState = uiState,
        onBack = onBack,
        onLoadMore = { viewModel.onEvent(ReviewsEvent.LoadMore) },
        onRetry = { viewModel.onEvent(ReviewsEvent.Retry) }
    )
}

@Composable
private fun ReviewsScreen(
    uiState: ReviewsUiState,
    onBack: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoadingInitial -> AppLoader()

            uiState.error != null && uiState.reviews.isEmpty() -> AppErrorComponent(onRetry = onRetry)

            else -> ReviewsList(
                uiState = uiState,
                onLoadMore = onLoadMore
            )
        }

        AppBackButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 48.dp)
        )
    }
}

@Composable
private fun ReviewsList(
    uiState: ReviewsUiState,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, uiState.reviews.size) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= info.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && uiState.canLoadMore) onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 104.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.all_reviews),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (uiState.reviews.isEmpty() && !uiState.isLoadingInitial) {
            item {
                Text(
                    text = stringResource(Res.string.no_reviews),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        itemsIndexed(uiState.reviews, key = { _, review -> review.id }) { index, review ->
            ReviewItem(review = review)
            if (index < uiState.reviews.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }

        if (uiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    var expanded by rememberSaveable(review.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.author,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                review.rating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${rating.toInt()}/10",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(
                text = formatIso8601Date(review.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = review.content,
            style = MaterialTheme.typography.bodySmall,
            maxLines = if (expanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4
        )

        if (review.content.length > 200) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(
                    text = if (expanded) stringResource(Res.string.show_less)
                    else stringResource(Res.string.read_more),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
