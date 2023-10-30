package features.movies.ui.movie_details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf
import ui.extansions.mirror

@Composable
fun MovieDetailsRoute(
    movieId: Int?,
    onBackPressed: () -> Unit,
    title: String,
) {
    val viewModel = koinViewModel(MovieDetailsViewModel::class) { parametersOf(movieId) }
    val uiState by viewModel.uiState.collectAsState()
    MovieDetailsScreen(
        uiState = uiState,
        title = title,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsViewModel.UiState,
    onBackPressed: () -> Unit,
    title: String,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            modifier = Modifier.mirror(),
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }
    ) {
        uiState.movieDetails?.let {
            KamelImage(
                resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${it.poster_path}"),
                contentDescription = "",
            )
        }
    }
}