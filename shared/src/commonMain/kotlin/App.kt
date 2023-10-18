import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import navigation.RootNavGraph

@Composable
fun App() {
    MaterialTheme {
        RootNavGraph()
    }
}

expect fun getPlatformName(): String