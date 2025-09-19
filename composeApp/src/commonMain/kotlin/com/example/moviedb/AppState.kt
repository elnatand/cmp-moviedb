package com.example.moviedb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.moviedb.feature.profile.navigation.navigateToProfile
import com.example.moviedb.feature.tvshows.navigation.navigateToTvShows
import com.example.moviedb.feature.movies.navigation.navigateToMovies
import com.example.moviedb.navigation.TopLevelDestination


@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController()
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
    private val navController: NavHostController,
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries
    private val bottomBarRoutes = TopLevelDestination.entries.map { it.route }

    val currentDestination: String?
        @Composable   get() {
            val backStackEntry = navController.currentBackStackEntry
            return backStackEntry?.id
        }

    @Composable
    fun shouldShowBottomBar(): Boolean {
        return true
      //  return currentDestination in bottomBarRoutes
    }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        when (topLevelDestination) {
            TopLevelDestination.MOVIES -> navController.navigateToMovies()
            TopLevelDestination.TV_SHOWS -> navController.navigateToTvShows()
            TopLevelDestination.PROFILE -> navController.navigateToProfile()
        }
    }
}