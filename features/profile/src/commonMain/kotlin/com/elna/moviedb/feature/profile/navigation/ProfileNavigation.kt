package com.elna.moviedb.feature.profile.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.profile.ui.ProfileScreen

fun EntryProviderScope<Route>.profileEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<ProfileRoute> {
        ProfileScreen()
    }
}
