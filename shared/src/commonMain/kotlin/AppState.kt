import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import ui.navigation.moviesRoute
import ui.navigation.tvShowsRoute

@Composable
fun rememberAppState(
    navController: Navigator = rememberNavigator(),
): AppState {
    return remember(
        navController,
    ) {
        AppState(
            navController = navController,
        )
    }
}

@Stable
class AppState(
    private val navController: Navigator,
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()
    private val bottomBarRoutes = TopLevelDestination.values().map { it.route }
    private val navOptions = NavOptions(
        // Launch the scene as single top
        launchSingleTop = true,
    )

    @Composable
    fun shouldShowBottomBar(): Boolean {
        val backStackEntry = navController.currentEntry.collectAsState("").value as? BackStackEntry
        return backStackEntry?.route?.route in bottomBarRoutes
    }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        when (topLevelDestination) {
            TopLevelDestination.MOVIES -> navController.navigate(moviesRoute, navOptions)
            TopLevelDestination.TV_SHOWS -> navController.navigate(tvShowsRoute, navOptions)
        }
    }
}