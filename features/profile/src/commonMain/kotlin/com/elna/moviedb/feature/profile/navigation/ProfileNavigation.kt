package com.elna.moviedb.feature.profile.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.elna.moviedb.feature.profile.ui.ProfileScreen

fun profileEntry(
    key: NavKey,
): NavEntry<NavKey> {
    return NavEntry(key = key) {
        ProfileScreen()
    }
}
