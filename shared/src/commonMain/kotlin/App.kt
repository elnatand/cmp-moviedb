import movies.MoviesPage
import movies.MoviesViewModel
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun App() {
    MaterialTheme {
        val moviesViewModel = getViewModel(Unit, viewModelFactory { MoviesViewModel() })
        MoviesPage(moviesViewModel)
    }
}

expect fun getPlatformName(): String