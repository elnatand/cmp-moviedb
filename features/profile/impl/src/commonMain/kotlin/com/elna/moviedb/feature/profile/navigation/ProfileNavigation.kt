package com.elna.moviedb.feature.profile.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.profile.api.navigation.ProfileRoute
import com.elna.moviedb.feature.profile.ui.ProfileScreen

/**
 * A [NavigationFactory] that provides the navigation graph for the profile feature.
 * This factory is responsible for creating the navigation entry for the [ProfileRoute],
 * which maps the route to the [ProfileScreen] composable.
 */
class ProfileNavigationFactory : NavigationFactory {
    override fun create(
        builder: EntryProviderScope<Route>,
        rootBackStack: SnapshotStateList<Route>,
        sharedTransitionScope: SharedTransitionScope
    ) = with(builder) {
        entry<ProfileRoute> {
            ProfileScreen()
        }
    }
}
