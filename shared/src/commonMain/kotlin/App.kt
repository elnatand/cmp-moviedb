import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun App() {
    MaterialTheme {
        val moviesViewModel = getViewModel(Unit, viewModelFactory { MoviesViewModel() })
        MoviesPage(moviesViewModel)
    }
}

@Composable
fun MoviesPage(viewModel: MoviesViewModel) {
    val uiState by viewModel.uiState.collectAsState()
}

expect fun getPlatformName(): String