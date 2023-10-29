import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import ui.navigation.moviesRoute
import ui.navigation.profileRoute
import ui.navigation.tvShowsRoute

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