package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun EntryProviderScope<Route>.tvShowsFlow(
    rootBackStack: SnapshotStateList<Route>,
    sharedTransitionScope: SharedTransitionScope
) {
    entry<TvShowsRoute> {
        TvShowsNavigation(
            rootBackStack = rootBackStack,
            startDestination = it.startAt,
            sharedTransitionScope = sharedTransitionScope
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TvShowsNavigation(
    rootBackStack: SnapshotStateList<Route>,
    startDestination: Route,
    sharedTransitionScope: SharedTransitionScope
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
                TvShowsScreen(
                    onClick = { tvShowId: Int, _: String, category: TvShowCategory ->
                        backStack.add(TvShowsRoute.TvShowDetailsRoute(tvShowId, category.name))
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
            }
            entry<TvShowsRoute.TvShowDetailsRoute> {
                TvShowDetailsScreen(
                    tvShowId = it.tvShowId,
                    category = it.category,
                    onCastMemberClick = { personId ->
                        rootBackStack.add(TvShowsRoute(startAt = it))
                        rootBackStack.add(PersonDetailsRoute(personId))
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
            }
        }
    )
}
