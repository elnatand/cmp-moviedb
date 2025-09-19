package com.example.moviedb.feature.profile.navigation

import androidx.navigation.NavHostController
import com.example.moviedb.feature.profile.ui.ProfileRoute
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder

const val profileRoute = "/profile"

fun NavHostController.navigateToProfile(){
    navigate(profileRoute)
}

fun RouteBuilder.profileScene(){
    scene(profileRoute) {
        ProfileRoute()
    }
}