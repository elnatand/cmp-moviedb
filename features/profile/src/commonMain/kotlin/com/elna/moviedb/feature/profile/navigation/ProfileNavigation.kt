package com.elna.moviedb.feature.profile.navigation

import androidx.navigation3.runtime.NavEntry
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.feature.profile.ui.ProfileScreen

fun profileEntry(
    key: Route,
): NavEntry<Route> {
    return NavEntry(key = key) {
        ProfileScreen()
    }
}
