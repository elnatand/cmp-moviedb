package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.model.MediaType
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
                    startDestination = it.startAt,
                    onCastMemberClick = { personId ->
                        rootBackStack.add(PersonDetailsRoute(personId))
                    }
                )
            }
            entry<TVShowsRoute> {
                TvShowsNavigation(
                    startDestination = it.startAt,
                    onCastMemberClick = { personId ->
                        rootBackStack.add(PersonDetailsRoute(personId))
                    }
                )
            }
            searchEntry(rootBackStack)
            profileEntry(rootBackStack)
            movieDetailsEntry(onCastMemberClick = { personId ->
                rootBackStack.add(PersonDetailsRoute(personId))
            })
            tvShowDetailsEntry(onCastMemberClick = { personId ->
                rootBackStack.add(PersonDetailsRoute(personId))
            })
            personDetailsEntry({ id, mediaType ->
                when (mediaType) {
                    MediaType.MOVIE -> rootBackStack.add(
                        MoviesRoute(
                            startAt = MoviesRoute.MovieDetailsRoute(
                                id
                            )
                        )
                    )

                    MediaType.TV -> rootBackStack.add(TVShowsRoute.TvShowDetailsRoute(id))
                }
            })
        }
    )
}

@Composable
fun MoviesNavigation(
    startDestination: Route,
    onCastMemberClick: (personId: Int) -> Unit
) {
    val backStack: SnapshotStateList<Route> = remember { mutableStateListOf(startDestination) }

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
    startDestination: Route,
    onCastMemberClick: (personId: Int) -> Unit = {}
) {
    val backStack: SnapshotStateList<Route> = remember { mutableStateListOf(startDestination) }

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
