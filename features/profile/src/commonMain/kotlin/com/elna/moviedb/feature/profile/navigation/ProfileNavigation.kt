package com.elna.moviedb.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.elna.moviedb.feature.profile.ui.ProfileScreen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("profile")
data object ProfileRoute

fun NavHostController.navigateToProfile(navOptions: NavOptions) {
    navigate(ProfileRoute, navOptions)
}

fun NavGraphBuilder.profileScene() {
    composable<ProfileRoute> { entry ->
        ProfileScreen()
    }
}