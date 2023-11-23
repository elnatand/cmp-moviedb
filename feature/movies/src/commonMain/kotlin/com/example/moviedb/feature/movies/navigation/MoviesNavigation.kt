package com.example.moviedb.feature.movies.navigation


import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsRoute
import com.example.moviedb.feature.movies.ui.movies.MoviesRoute
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path

const val moviesRoute = "/movies"
const val movieDetailsRoute = "/movie_details"
const val MOVIE_ID = "movieId"
const val MOVIE_TITLE = "movie_title"

fun Navigator.navigateToMovies(navOptions: NavOptions) {
    navigate(moviesRoute, navOptions)
}

fun RouteBuilder.moviesScene(navigator: Navigator, navOptions: NavOptions) {
    scene(moviesRoute) {
        MoviesRoute { movieId, title ->
            navigator.navigate("$movieDetailsRoute/$movieId/$title", navOptions)
        }
    }
}

fun RouteBuilder.movieDetailsScene(navigator: Navigator) {
    scene("$movieDetailsRoute/{$MOVIE_ID}/{$MOVIE_TITLE}") {
        val movieId: Int = it.path(MOVIE_ID) ?: 0
        val title: String = it.path(MOVIE_TITLE) ?: ""
        MovieDetailsRoute(
            movieId = movieId,
            title = title,
            onBackPressed = { navigator.goBack() }
        )
    }
}