package com.elna.moviedb.core.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope

/**
 * An interface for factories that define and register navigation destinations.
 * Implementations of this interface are responsible for setting up the navigation graph
 * for a specific feature or a set of related screens.
 */
interface NavigationFactory {
    fun create(
        builder: EntryProviderScope<Route>,
        rootBackStack: SnapshotStateList<Route>,
        sharedTransitionScope: SharedTransitionScope
    )
}
