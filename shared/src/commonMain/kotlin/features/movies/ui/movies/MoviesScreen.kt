package features.movies.ui.movies

import ui.strings.Strings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun MoviesRoute(
    onClick: (Int) -> Unit
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
    uiState: MoviesViewModel.UiState,
    onClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = Strings.movies.get()) },
            )
        }
    ) {
        AnimatedVisibility(
            visible = uiState.movies.isNotEmpty(),
            modifier = Modifier.padding(it)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
                content = {
                    items(uiState.movies) {
                        MovieCell(
                            movie = it,
                            onClick = onClick
                        )
                    }
                }
            )
        }
    }
}