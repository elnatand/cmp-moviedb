package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

fun EntryProviderScope<Route>.tvShowsFlow(
    rootBackStack: SnapshotStateList<Route>
) {
    entry<TvShowsRoute> {
        TvShowsNavigation(
            rootBackStack = rootBackStack,
            startDestination = it.startAt
        )
    }
}

@Composable
private fun TvShowsNavigation(
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
            entry<TvShowsRoute.TvShowsListRoute> {
                TvShowsScreen(onClick = { tvShowId, _ ->
                    backStack.add(TvShowsRoute.TvShowDetailsRoute(tvShowId))
                })
            }
            entry<TvShowsRoute.TvShowDetailsRoute> {
                TvShowDetailsScreen(
                    tvShowId = it.tvShowId,
                    onCastMemberClick = { personId ->
                        rootBackStack.add(PersonDetailsRoute(personId))
                    }
                )
            }
        }
    )
}
