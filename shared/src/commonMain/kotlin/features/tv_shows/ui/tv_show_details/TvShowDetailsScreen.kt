package features.tv_shows.ui.tv_show_details

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun TvShowDetailsRoute(
    onClick: (Int) -> Unit
) {
    val viewModel = koinViewModel(TvShowDetailsViewModel::class)
    val uiState by viewModel.uiState.collectAsState()
    TvShowDetailsScreen(
        uiState = uiState,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailsScreen(
    uiState: TvShowDetailsViewModel.UiState,
    onClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "TV Show Details") },
            )
        }
    ) {

    }

}