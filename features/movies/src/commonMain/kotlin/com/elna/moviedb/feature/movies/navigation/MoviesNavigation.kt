package com.elna.moviedb.feature.movies.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen


fun EntryProviderScope<Route>.moviesFlow(
    rootBackStack: SnapshotStateList<Route>
) {
    entry<MoviesRoute> {
        MoviesNavigation(
            rootBackStack = rootBackStack,
            startDestination = it.startAt,
        )
    }
}


@Composable
private fun MoviesNavigation(
    rootBackStack: SnapshotStateList<Route>,
    startDestination: Route,
) {
    val backStack: SnapshotStateList<Route> = remember { mutableStateListOf(startDestination) }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<MoviesRoute.MoviesListRoute> {
                MoviesScreen(onClick = { movieId, _ ->
                    backStack.add(MoviesRoute.MovieDetailsRoute(movieId))
                })
            }
            entry<MoviesRoute.MovieDetailsRoute> {
                MovieDetailsScreen(
                    movieId = it.movieId,
                    onCastMemberClick = { personId ->
                        rootBackStack.add(PersonDetailsRoute(personId))
                    }
                )
            }
        }
    )
}
