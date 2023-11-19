package features.profile.navigation

import features.profile.ui.ProfileRoute
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