import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.NavigationBar
import androidx.compose.ui.graphics.vector.ImageVector
import ui.navigation.RootNavGraph
import ui.navigation.moviesRoute
import ui.navigation.tvShowsRoute

enum class TopLevelDestination(
    val icon: ImageVector,
    val title: String,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.BrokenImage,
        title = "Movies",
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Clear,
        title = "TV Shows",
        route = tvShowsRoute
    ),
}