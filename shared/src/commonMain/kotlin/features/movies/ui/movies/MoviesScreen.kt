package features.movies.ui.movies

import ui.strings.Strings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import features.movies.model.Movie
import moe.tlaster.precompose.koin.koinViewModel
import ui.design_system.Loader
import ui.model.State
import ui.model.UiState

@Composable
fun MoviesRoute(
    onClick: (movieId: Int, title: String) -> Unit
) {
    val viewModel = koinViewModel(MoviesViewModel::class)
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
                title = { Text(text = Strings.movies.get()) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.state) {
                State.LOADING -> {
                    Loader(modifier = Modifier.padding(paddingValues))
                }

                State.SUCCESS -> {
                    uiState.data?.let {
                        SuccessState(it, paddingValues, onClick)
                    } ?: ErrorState()
                }

                State.ERROR -> ErrorState()
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
    paddingValues: PaddingValues,
    onClick: (Int, String) -> Unit
) {
    AnimatedVisibility(
        visible = movies.isNotEmpty(),
        modifier = Modifier.padding(paddingValues)
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