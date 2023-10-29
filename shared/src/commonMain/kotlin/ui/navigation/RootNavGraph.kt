package ui.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import features.movies.ui.movie_details.MovieDetailsRoute
import features.movies.ui.movies.MoviesRoute
import features.profile.ui.ProfileRoute
import features.tv_shows.ui.tv_show_details.TvShowDetailsRoute
import features.tv_shows.ui.tv_shows.TvShowsRoute
import moe.tlaster.precompose.navigation.NavOptions

const val moviesRoute = "/movies"
const val movieDetailsRoute = "/movie_details"

const val tvShowsRoute = "/tv_shows"
const val tvShowDetailsRoute = "/tv_show_details"
const val profileRoute = "/profile"

@Composable
fun RootNavGraph(navigator: Navigator = rememberNavigator()) {
    val navOptions = NavOptions(
        // Launch the scene as single top
        launchSingleTop = true,
    )
    NavHost(
        navigator = navigator,
        initialRoute = moviesRoute,
    ) {
        scene(moviesRoute) {
            MoviesRoute {
                navigator.navigate("$movieDetailsRoute/$it", navOptions)
            }
        }
        scene("$movieDetailsRoute/{movieId}") {
            val movieId: Int? = it.path("movieId")
            MovieDetailsRoute(
                movieId = movieId,
                onBackPressed = { navigator.goBack() }
            )
        }
        scene(tvShowsRoute) {
            TvShowsRoute {
                navigator.navigate("$tvShowDetailsRoute/$it", navOptions)
            }
        }
        scene("$tvShowDetailsRoute/{tvShowId}") {
            val tvShowId: Int? = it.path("tvShowId")
            TvShowDetailsRoute(
                tvShowId = tvShowId,
                onBackPressed = { navigator.goBack() }
            )
        }
        scene(profileRoute) {
            ProfileRoute()
        }
    }
}
