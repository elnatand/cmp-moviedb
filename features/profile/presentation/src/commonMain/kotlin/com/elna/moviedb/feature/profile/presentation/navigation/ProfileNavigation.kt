package com.elna.moviedb.feature.profile.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.profile.presentation.ui.ProfileScreen

fun EntryProviderScope<Route>.profileEntry() {
    entry<ProfileRoute> {
        ProfileScreen()
    }
}
