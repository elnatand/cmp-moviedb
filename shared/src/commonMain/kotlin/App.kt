import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import navigation.RootNavGraph

@Composable
fun App() {
    MaterialTheme {
        RootNavGraph()
//        val moviesViewModel = getViewModel(Unit, viewModelFactory { MoviesViewModel() })
//        MoviesPage(moviesViewModel)
    }
}

expect fun getPlatformName(): String