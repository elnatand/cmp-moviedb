package com.example.moviedb.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.moviedb.feature.profile.ui.ProfileRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val profileRoute = "/profile"

@Serializable
@SerialName("profile")
data object PROFILE

fun NavHostController.navigateToProfile(){
    navigate(PROFILE)
}

fun NavGraphBuilder.profileScene(){
    composable<PROFILE> { entry ->
        ProfileRoute()
    }
}