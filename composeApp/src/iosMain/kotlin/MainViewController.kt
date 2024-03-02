import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.lifecycle.ApplicationLifecycle
import com.example.moviedb.App
import com.example.moviedb.RootComponent

fun MainViewController() = ComposeUIViewController {
    val root = remember {
        RootComponent(DefaultComponentContext(ApplicationLifecycle()))
    }
    App(root)
}