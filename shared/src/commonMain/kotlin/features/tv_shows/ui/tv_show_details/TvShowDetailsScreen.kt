package features.tv_shows.ui.tv_show_details

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
fun TvShowDetailsRoute(
    tvShowId: Int?,
    onBackPressed: () -> Unit,
) {
    val viewModel = koinViewModel(TvShowDetailsViewModel::class) { parametersOf(tvShowId) }
    val uiState by viewModel.uiState.collectAsState()
    TvShowDetailsScreen(
        uiState = uiState,
        onBackPressed = onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailsScreen(
    uiState: TvShowDetailsViewModel.UiState,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "TV Show Details") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            modifier = Modifier.mirror(),
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        uiState.tvShowDetails?.let {
            KamelImage(
                resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${it.poster_path}"),
                contentDescription = "",
            )
        }
    }
}