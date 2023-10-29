package features.movies.navigation


import features.movies.ui.movie_details.MovieDetailsRoute
import features.movies.ui.movies.MoviesRoute
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path

const val moviesRoute = "/movies"
const val movieDetailsRoute = "/movie_details"
const val MOVIE_ID = "movieId"

fun Navigator.navigateToMovies(navOptions: NavOptions){
    navigate(moviesRoute, navOptions)
}

fun RouteBuilder.moviesScene(navigator: Navigator, navOptions: NavOptions) {
    scene(moviesRoute) {
        MoviesRoute {
            navigator.navigate("$movieDetailsRoute/$it", navOptions)
        }
    }
}

fun RouteBuilder.movieDetailsScene(navigator: Navigator) {
    scene("$movieDetailsRoute/{$MOVIE_ID}") {
        val movieId: Int? = it.path(MOVIE_ID)
        MovieDetailsRoute(
            movieId = movieId,
            onBackPressed = { navigator.goBack() }
        )
    }
}