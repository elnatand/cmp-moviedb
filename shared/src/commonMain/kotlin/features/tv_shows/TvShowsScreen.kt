package features.tv_shows

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun TvShowsRoute(
    onClick: (Int) -> Unit
) {
    val viewModel = getViewModel(Unit, viewModelFactory { TvShowsViewModel() })
    val uiState by viewModel.uiState.collectAsState()
    TvShowsScreen(
        uiState = uiState,
        onClick = onClick
    )
}

@Composable
fun TvShowsScreen(
    uiState: TvShowsViewModel.UiState,
    onClick: (Int) -> Unit
) {
    Text("Not implemented yet")
//    AnimatedVisibility(uiState.movies.isNotEmpty()) {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            horizontalArrangement = Arrangement.spacedBy(5.dp),
//            verticalArrangement = Arrangement.spacedBy(5.dp),
//            modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
//            content = {
//
//            }
//        )
//    }
}