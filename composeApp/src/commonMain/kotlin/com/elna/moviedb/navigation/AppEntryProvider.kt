package com.elna.moviedb.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.movies.navigation.moviesFlow
import com.elna.moviedb.feature.person.presentation.navigation.personDetailsEntry
import com.elna.moviedb.feature.profile.presentation.navigation.profileEntry
import com.elna.moviedb.feature.search.presentation.navigation.searchEntry
import com.elna.moviedb.feature.tvshows.presentation.navigation.tvShowsFlow

/**
 * The single route → screen registry, shared by both shells: [RootNavGraph]'s NavDisplay
 * (Android and iOS < 26, [navigator] mutates the Compose back stack) and the iOS 26+
 * Liquid Glass shell's per-screen view controllers ([ScreenContent], [navigator] forwards
 * to SwiftUI's NavigationStack).
 *
 * [sharedTransitionScope] is null under the native shell, where screens live in separate
 * view controllers and can't share a transition scope — the native push/pop animation
 * takes the place of shared elements.
 */
fun appEntryProvider(
    navigator: Navigator,
    sharedTransitionScope: SharedTransitionScope? = null,
): (Route) -> NavEntry<Route> = entryProvider {

    moviesFlow(navigator, sharedTransitionScope)

    tvShowsFlow(navigator, sharedTransitionScope)

    searchEntry(navigator)

    personDetailsEntry(navigator)

    profileEntry()
}
