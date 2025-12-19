package com.elna.moviedb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.navigation.TopLevelDestination

@Composable
fun rememberAppState(
    navBackStack: SnapshotStateList<Route> = remember { mutableStateListOf(MoviesRoute()) }
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
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                navBackStack.last() == topLevelDestination.route
            }
        }

    @Composable
    fun shouldShowBottomBar(): Boolean {
        return navBackStack.last() in bottomBarRoutes
    }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        when (topLevelDestination) {
            TopLevelDestination.MOVIES -> navBackStack.add(MoviesRoute())
            TopLevelDestination.TV_SHOWS -> navBackStack.add(TvShowsRoute())
            TopLevelDestination.SEARCH -> navBackStack.add(SearchRoute)
            TopLevelDestination.PROFILE -> navBackStack.add(ProfileRoute)
        }
    }
}