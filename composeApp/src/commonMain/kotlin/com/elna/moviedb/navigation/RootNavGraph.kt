package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TVShowsRoute
import com.elna.moviedb.feature.movies.navigation.movieDetailsEntry
import com.elna.moviedb.feature.movies.navigation.moviesEntry
import com.elna.moviedb.feature.person.navigation.personDetailsEntry
import com.elna.moviedb.feature.profile.navigation.profileEntry
import com.elna.moviedb.feature.search.navigation.searchEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowDetailsEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowsEntry

@Composable
fun RootNavGraph(
    rootBackStack: SnapshotStateList<Route>,
) {
    NavDisplay(
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<MoviesRoute> {
                MoviesNavigation(
                    onCastMemberClick = { personId ->
                        rootBackStack.add(PersonDetailsRoute(personId))
                    }
                )
            }
            entry<TVShowsRoute> {
                TvShowsNavigation(
                    onCastMemberClick = { personId ->
                        rootBackStack.add(PersonDetailsRoute(personId))
                    }
                )
            }
            searchEntry(rootBackStack)
            profileEntry(rootBackStack)
            personDetailsEntry(rootBackStack)
        }
    )
}

@Composable
fun MoviesNavigation(
    onCastMemberClick: (personId: Int) -> Unit
) {
    val backStack: SnapshotStateList<Route> =
        remember { mutableStateListOf(MoviesRoute.MoviesListRoute) }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            moviesEntry(backStack)
            movieDetailsEntry(onCastMemberClick)
        }
    )
}

@Composable
fun TvShowsNavigation(
    onCastMemberClick: (personId: Int) -> Unit = {}
) {
    val backStack: SnapshotStateList<Route> =
        remember { mutableStateListOf(TVShowsRoute.TvShowsListRoute) }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            tvShowsEntry(backStack)
            tvShowDetailsEntry(onCastMemberClick)
        }
    )
}
