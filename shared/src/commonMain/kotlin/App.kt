import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import ui.design_system.NavigationBar
import ui.navigation.RootNavGraph

@Composable
fun App(
    appState: MdbAppState = rememberMdbAppState()
) {

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

expect fun getPlatformName(): String