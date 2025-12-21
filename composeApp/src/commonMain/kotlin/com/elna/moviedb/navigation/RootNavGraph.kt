package com.elna.moviedb.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.navigation.moviesFlow
import com.elna.moviedb.feature.person.navigation.personDetailsEntry
import com.elna.moviedb.feature.profile.navigation.profileEntry
import com.elna.moviedb.feature.search.navigation.searchEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowsFlow

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RootNavGraph(
    rootBackStack: SnapshotStateList<Route>,
) {
    SharedTransitionLayout {
        NavDisplay(
            backStack = rootBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {

                moviesFlow(
                    rootBackStack = rootBackStack,
                    sharedTransitionScope = this@SharedTransitionLayout
                ) // nested graph

                tvShowsFlow(
                    rootBackStack = rootBackStack,
                    sharedTransitionScope = this@SharedTransitionLayout
                ) // nested graph

                searchEntry(rootBackStack)

                personDetailsEntry(
                    rootBackStack = rootBackStack,
                    sharedTransitionScope = this@SharedTransitionLayout
                )

                profileEntry()
            }
        )
    }
}
