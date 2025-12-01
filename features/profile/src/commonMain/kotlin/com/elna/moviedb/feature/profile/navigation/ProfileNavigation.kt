package com.elna.moviedb.feature.profile.navigation

import androidx.navigation3.runtime.NavEntry
import com.elna.moviedb.feature.profile.ui.ProfileScreen

fun profileEntry(
    key: Any,
): NavEntry<Any> {
    return NavEntry(key = key) {
        ProfileScreen()
    }
}
