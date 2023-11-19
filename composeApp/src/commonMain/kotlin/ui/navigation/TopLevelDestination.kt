package ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Theaters
//import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import features.movies.navigation.moviesRoute
import features.profile.navigation.profileRoute
import features.tv_shows.navigation.tvShowsRoute
import ui.strings.Strings


enum class TopLevelDestination(
    val icon: ImageVector,
    val titleRes: Strings,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Person,
        titleRes = Strings.movies,
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Person,
        titleRes = Strings.tv_shows,
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        titleRes = Strings.profile,
        route = profileRoute
    ),
}