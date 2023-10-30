package ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import features.movies.navigation.moviesRoute
import features.profile.navigation.profileRoute
import features.tv_shows.navigation.tvShowsRoute


enum class TopLevelDestination(
    val icon: ImageVector,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        route = profileRoute
    ),
}