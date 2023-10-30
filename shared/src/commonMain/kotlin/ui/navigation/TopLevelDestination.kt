package ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import features.movies.navigation.moviesRoute
import features.profile.navigation.profileRoute
import features.tv_shows.navigation.tvShowsRoute
import ui.strings.Strings


enum class TopLevelDestination(
    val icon: ImageVector,
    val title: String,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        title = Strings.movies.get(),
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        title = Strings.tv_shows.get(),
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        title = Strings.profile.get(),
        route = profileRoute
    ),
}