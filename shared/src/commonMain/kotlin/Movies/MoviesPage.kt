package Movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MoviesPage(viewModel: MoviesViewModel) {
    val uiState by viewModel.uiState.collectAsState()
}