import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext
import ui.design_system.NavigationBar
import ui.navigation.RootNavGraph

@Composable
fun App(
    appState: AppState = rememberAppState()
) {
    KoinContext {
        MaterialTheme {
            Scaffold(
                bottomBar = {
                    if (appState.shouldShowBottomBar()) {
                        NavigationBar(
                            topLevelDestinations = appState.topLevelDestinations,
                            onClick = appState::navigateToTopLevelDestination
                        )
                    }
                }
            ) {
                RootNavGraph()
            }
        }
    }
}

expect fun getPlatformName(): String

expect val platformLanguage: String?