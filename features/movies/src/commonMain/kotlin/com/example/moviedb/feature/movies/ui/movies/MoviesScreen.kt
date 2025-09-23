package com.example.moviedb.feature.movies.ui.movies


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.ui.design_system.Loader
import com.example.moviedb.feature.movies.model.MoviesUiState
import com.example.moviedb.resources.Res
import com.example.moviedb.resources.movies
import com.example.moviedb.resources.network_error
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MoviesRoute(
    onClick: (movieId: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel<MoviesViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesScreen(
        state = uiState.state,
        hasMorePages = uiState.hasMorePages,
        onClick = onClick,
        onLoadNextPage = viewModel::loadNextPage
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    state: MoviesUiState.State,
    hasMorePages: Boolean,
    onClick: (Int, String) -> Unit,
    onLoadNextPage: () -> Unit
) {
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
            when (state) {
                MoviesUiState.State.Loading -> Loader()
                MoviesUiState.State.Error -> ErrorState()
                is MoviesUiState.State.Success -> SuccessState(
                    movies = state.movies,
                    hasMorePages = hasMorePages,
                    onClick = onClick,
                    onLoadNextPage = onLoadNextPage
                )
            }
        }
    }
}

@Composable
private fun ErrorState() {
    Text(
        text = stringResource(Res.string.network_error),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun SuccessState(
    movies: List<Movie>,
    hasMorePages: Boolean,
    onClick: (Int, String) -> Unit,
    onLoadNextPage: () -> Unit
) {
    AnimatedVisibility(
        visible = movies.isNotEmpty(),
    ) {
        val gridState = rememberLazyGridState()

        // Detect when user reaches the bottom
        val shouldLoadMore by remember {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val totalItemsNumber = layoutInfo.totalItemsCount
                val lastVisibleItemIndex =
                    (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
                println("lastVisibleItemIndex = ${lastVisibleItemIndex}")
                println("totalItemsNumber = ${totalItemsNumber}")
                lastVisibleItemIndex + 1 > (totalItemsNumber) && hasMorePages
            }
        }

        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) {
                println("shouldLoadMore")
                onLoadNextPage()
            }
        }

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
            content = {
                items(movies) {
                    MovieCell(
                        movie = it,
                        onClick = onClick
                    )
                }
            }
        )
    }
}