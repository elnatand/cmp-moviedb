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
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MovieDetailsRoute(
    movieId: Int?,
    onBackPressed: () -> Unit,
) {
    val viewModel = getViewModel(Unit, viewModelFactory { MovieDetailsViewModel() })
    val uiState by viewModel.uiState.collectAsState()
    viewModel.getMovieDetails(movieId ?: 0)
    MovieDetailsScreen(
        uiState = uiState,
        onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsViewModel.UiState,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Movie Details") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
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