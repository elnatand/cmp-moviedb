package ui.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import features.movies.ui.movie_details.MovieDetailsRoute
import features.movies.ui.movies.MoviesRoute
import features.tv_shows.TvShowsRoute

const val moviesRoute = "/movies"
const val tvShowsRoute = "/tv_shows"
const val movieDetailsRoute = "/movie_details"

@Composable
fun RootNavGraph(navigator: Navigator = rememberNavigator()) {
    NavHost(
        navigator = navigator,
        initialRoute = moviesRoute,
    ) {
        scene(moviesRoute) {
            MoviesRoute{
                navigator.navigate("$movieDetailsRoute/$it")
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
                //TODO to be implemented
            }
        }
    }
}
