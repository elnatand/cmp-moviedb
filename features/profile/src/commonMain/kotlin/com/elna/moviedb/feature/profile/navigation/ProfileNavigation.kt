package com.elna.moviedb.feature.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.navigation.ProfileRoute
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.profile.ui.ProfileScreen

fun EntryProviderScope<Route>.profileEntry() {
    entry<ProfileRoute> {
        ProfileScreen()
    }
}
