import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.PreComposeApp
import org.koin.compose.KoinContext
import ui.design_system.NavigationBar
import ui.navigation.RootNavGraph

@Composable
fun App() {
    PreComposeApp {
      val appState: AppState = rememberAppState()
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

}

expect fun getPlatformName(): String

expect val platformLanguage: String?