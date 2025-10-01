package com.elna.moviedb.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.feature.profile.ui.ProfileScreen


fun NavHostController.navigateToProfile(navOptions: NavOptions) {
    navigate(ProfileRoute, navOptions)
}

fun NavGraphBuilder.profileScene() {
    composable<ProfileRoute> { entry ->
        ProfileScreen()
    }
}