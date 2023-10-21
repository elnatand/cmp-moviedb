package features.movies.ui.movie_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MovieDetailsRoute(
    movieId: Int?
) {
    val viewModel = getViewModel(Unit, viewModelFactory { MovieDetailsViewModel() })
    val uiState by viewModel.uiState.collectAsState()
    viewModel.getMovieDetails(movieId ?: 0)
    MovieDetailsScreen(
        uiState = uiState,
    )
}

@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsViewModel.UiState,
) {
    uiState.movieDetails?.let {
        KamelImage(
            resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${it.poster_path}"),
            contentDescription = "",
        )
    }
}