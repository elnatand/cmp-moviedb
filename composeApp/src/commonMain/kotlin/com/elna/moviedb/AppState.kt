package com.elna.moviedb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.navigation.TopLevelDestination
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

private val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(MoviesRoute::class)
            subclass(MovieDetailsRoute::class)
            subclass(TvShowsRoute::class)
            subclass(TvShowDetailsRoute::class)
            subclass(SearchRoute::class)
            subclass(ProfileRoute::class)
            subclass(PersonDetailsRoute::class)
        }
    }
}

@Composable
fun rememberAppState(
    navBackStack: NavBackStack<NavKey> = rememberNavBackStack(
        configuration = navSavedStateConfiguration,
        MoviesRoute
    )
): AppState {
    return remember(navBackStack) {
        AppState(navBackStack = navBackStack)
    }
}

@Stable
class AppState(
    val navBackStack: NavBackStack<NavKey>,
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
            TopLevelDestination.MOVIES -> navBackStack.add(MoviesRoute)
            TopLevelDestination.TV_SHOWS -> navBackStack.add(TvShowsRoute)
            TopLevelDestination.SEARCH -> navBackStack.add(SearchRoute)
            TopLevelDestination.PROFILE -> navBackStack.add(ProfileRoute)
        }
    }
}