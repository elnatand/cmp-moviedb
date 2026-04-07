package com.elna.moviedb.feature.movies.ui.movies

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toPosterUrl
import com.elna.moviedb.feature.movies.model.MoviesEvent
import com.elna.moviedb.feature.movies.model.MoviesUiAction
import com.elna.moviedb.feature.movies.model.MoviesUiState
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.now_playing
import com.elna.moviedb.resources.popular
import com.elna.moviedb.resources.search_hint
import com.elna.moviedb.resources.top_rated
import com.elna.moviedb.resources.view_details
import com.elna.moviedb.resources.watch_now
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Stitch design: CineDB Home Redesign palette
private val AppDark = Color(0xFF131318)
private val AppSurface = Color(0xFF1F1F25)
private val AppSurfaceHigh = Color(0xFF2A292F)
private val AppPrimary = Color(0xFFB4C5FF)
private val AppPrimaryContainer = Color(0xFF195DE6)
private val AppOnSurface = Color(0xFFE4E1E9)
private val AppTextGray = Color(0xFFC7C5D3)

@Composable
fun MoviesScreen(
    onClick: (movieId: Int, title: String, category: MovieCategory) -> Unit,
    onSearchClick: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val viewModel = koinViewModel<MoviesViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesScreen(
        uiState = uiState,
        onClick = onClick,
        onEvent = viewModel::onEvent,
        uiActions = viewModel.uiAction,
        onSearchClick = onSearchClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesScreen(
    uiState: MoviesUiState,
    onClick: (Int, String, MovieCategory) -> Unit,
    onEvent: (MoviesEvent) -> Unit,
    uiActions: Flow<MoviesUiAction>,
    onSearchClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }

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
        containerColor = AppDark,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.state) {
                MoviesUiState.State.ERROR -> {
                    AppErrorComponent(onRetry = { onEvent(MoviesEvent.Retry) })
                }
                else -> {
                    MoviesContent(
                        uiState = uiState,
                        onClick = onClick,
                        onEvent = onEvent,
                        onSearchClick = onSearchClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesContent(
    uiState: MoviesUiState,
    onClick: (id: Int, title: String, category: MovieCategory) -> Unit,
    onEvent: (MoviesEvent) -> Unit,
    onSearchClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val heroMovie = uiState.getMovies(MovieCategory.NOW_PLAYING).firstOrNull()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onEvent(MoviesEvent.Refresh) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppDark)
                .verticalScroll(rememberScrollState())
        ) {
            MovieSearchBar(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            heroMovie?.let { movie ->
                HeroSection(
                    movie = movie,
                    onWatchNow = { onClick(movie.id, movie.title, MovieCategory.NOW_PLAYING) },
                    onViewDetails = { onClick(movie.id, movie.title, MovieCategory.NOW_PLAYING) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                )
            }

            MovieCategory.entries.forEach { category ->
                MoviesSection(
                    category = category,
                    title = stringResource(getCategoryStringResource(category)),
                    movies = uiState.getMovies(category),
                    onClick = onClick,
                    isLoading = uiState.isLoading(category),
                    onLoadMore = { onEvent(MoviesEvent.LoadNextPage(category)) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MovieSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(AppSurface, RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = AppTextGray,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = stringResource(Res.string.search_hint),
            color = AppTextGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun HeroSection(
    movie: Movie,
    onWatchNow: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        ImageLoader(
            imageUrl = movie.posterPath.toPosterUrl(),
            modifier = Modifier.fillMaxSize()
        )

        // Stitch gradient: #131318 at bottom → transparent at top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.55f to AppDark.copy(alpha = 0.6f),
                            1.0f to AppDark
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = movie.title.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AppOnSurface,
                letterSpacing = 3.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onWatchNow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPrimaryContainer,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = stringResource(Res.string.watch_now),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppPrimary),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.view_details),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun getCategoryStringResource(category: MovieCategory) = when (category) {
    MovieCategory.POPULAR -> Res.string.popular
    MovieCategory.TOP_RATED -> Res.string.top_rated
    MovieCategory.NOW_PLAYING -> Res.string.now_playing
}

@Composable
private fun MoviesSection(
    category: MovieCategory,
    title: String,
    movies: List<Movie>,
    onClick: (id: Int, title: String, category: MovieCategory) -> Unit,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val listState = rememberLazyListState()
    val currentIsLoading by rememberUpdatedState(isLoading)

    LaunchedEffect(listState, movies.isNotEmpty()) {
        if (movies.isEmpty()) return@LaunchedEffect
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(AppSurface, RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppOnSurface,
            )
        }

        if (movies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                AppLoader()
            }
        } else {
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = movies,
                    key = { "${category.name}_${it.id}" }
                ) { movie ->
                    MovieTile(
                        category = category,
                        movie = movie,
                        onClick = { id, title -> onClick(id, title, category) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
        }
    }
}
