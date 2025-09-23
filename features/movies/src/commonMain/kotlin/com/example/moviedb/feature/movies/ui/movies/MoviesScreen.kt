package com.example.moviedb.feature.movies.ui.movies


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.model.State
import com.example.moviedb.core.model.UiState
import com.example.moviedb.core.ui.design_system.Loader
import com.example.moviedb.resources.Res
import com.example.moviedb.resources.movies
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun MoviesRoute(
    onClick: (movieId: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel<MoviesViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    MoviesScreen(
        uiState = uiState,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    uiState: UiState<List<Movie>>,
    onClick: (Int, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.movies)) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.state) {
                State.LOADING -> Loader(modifier = Modifier.padding(paddingValues))
                State.ERROR -> ErrorState()
                State.SUCCESS -> uiState.data?.let {
                    SuccessState(it, onClick)
                } ?: ErrorState()
            }
        }
    }
}

@Composable
private fun ErrorState() {

}

@Composable
private fun SuccessState(
    movies: List<Movie>,
    onClick: (Int, String) -> Unit
) {
    AnimatedVisibility(
        visible = movies.isNotEmpty(),
    ) {
        LazyVerticalGrid(
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