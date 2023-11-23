package com.example.moviedb.feature.profile.navigation

import com.example.moviedb.feature.profile.ui.ProfileRoute
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder

const val profileRoute = "/profile"

fun Navigator.navigateToProfile(navOptions: NavOptions){
    navigate(profileRoute, navOptions)
}

fun RouteBuilder.profileScene(){
    scene(profileRoute) {
        ProfileRoute()
    }
}