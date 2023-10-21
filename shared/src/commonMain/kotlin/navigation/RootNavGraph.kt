package navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import features.movies.ui.movie_details.MovieDetailsRoute
import features.movies.ui.movies.MoviesRoute

@Composable
fun RootNavGraph(navigator: Navigator = rememberNavigator()) {
    NavHost(
        navigator = navigator,
        initialRoute = "/home",
    ) {
        scene("/home") {
            MoviesRoute{
                navigator.navigate("/movie_details/$it")
            }
        }
        scene("/movie_details/{movieId}") {
            val movieId: Int? = it.path("movieId")
            MovieDetailsRoute(movieId)
        }
    }
}
