package com.elna.moviedb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.movie.api.navigation.MoviesRoute
import com.elna.moviedb.feature.profile.api.navigation.ProfileRoute
import com.elna.moviedb.feature.search.api.navigation.SearchRoute
import com.elna.moviedb.feature.tvshows.api.navigation.TvShowsRoute
import com.elna.moviedb.navigation.TopLevelDestination

@Composable
fun rememberAppState(
    navBackStack: SnapshotStateList<Route> = remember { mutableStateListOf(MoviesRoute.MoviesListRoute) }
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
        val targetRoute = when (topLevelDestination) {
            TopLevelDestination.MOVIES -> MoviesRoute.MoviesListRoute
            TopLevelDestination.TV_SHOWS -> TvShowsRoute.TvShowsListRoute
            TopLevelDestination.SEARCH -> SearchRoute
            TopLevelDestination.PROFILE -> ProfileRoute
        }

        if (navBackStack.lastOrNull() == targetRoute) return

        navBackStack.clear()
        navBackStack.add(targetRoute)
    }
}