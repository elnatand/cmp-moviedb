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
    val title: String,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        title = "Movies",
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        title = "TV Shows",
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        title = "Profile",
        route = profileRoute
    ),
}