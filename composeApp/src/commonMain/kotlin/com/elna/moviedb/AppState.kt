package com.elna.moviedb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.navigation.TopLevelDestination
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val navBackStackJson = Json { ignoreUnknownKeys = true }
private val routeListSerializer = ListSerializer(Route.serializer())

/**
 * Saves/restores the navigation back stack across Android process death by serializing the
 * (sealed, [kotlinx.serialization.Serializable]) [Route] list to a String. Restore returns a
 * fresh [SnapshotStateList] so the stack keeps participating in Compose's snapshot system.
 */
private val NavBackStackSaver: Saver<SnapshotStateList<Route>, String> = Saver(
    save = { navBackStackJson.encodeToString(routeListSerializer, it.toList()) },
    restore = { encoded ->
        mutableStateListOf<Route>().apply {
            addAll(navBackStackJson.decodeFromString(routeListSerializer, encoded))
        }
    }
)

@Composable
fun rememberAppState(
    navBackStack: SnapshotStateList<Route> = rememberSaveable(saver = NavBackStackSaver) {
        mutableStateListOf(MoviesRoute.MoviesListRoute)
    }
): AppState {
    return remember(navBackStack) {
        AppState(navBackStack = navBackStack)
    }
}

@Stable
class AppState(
    val navBackStack: SnapshotStateList<Route>,
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
    private val bottomBarRoutes = TopLevelDestination.entries.map { it.route }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            val currentRoute = navBackStack.lastOrNull() ?: return null
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentRoute == topLevelDestination.route
            }
        }

    @Composable
    fun shouldShowBottomBar(): Boolean {
        return navBackStack.lastOrNull() in bottomBarRoutes
    }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        // The route the enum already carries — no hardcoded branch to keep in sync.
        val targetRoute = topLevelDestination.route

        // Already on this top-level destination: nothing to do.
        if (navBackStack.lastOrNull() == targetRoute) return

        val targetIndex = navBackStack.indexOf(targetRoute)
        if (targetIndex >= 0) {
            // Destination already in the stack (e.g. its root sits below detail screens):
            // pop back to it instead of wiping the stack, preserving navigation history.
            while (navBackStack.size > targetIndex + 1) {
                navBackStack.removeAt(navBackStack.lastIndex)
            }
        } else {
            // Brand-new top-level destination: reset to its root.
            navBackStack.clear()
            navBackStack.add(targetRoute)
        }
    }
}