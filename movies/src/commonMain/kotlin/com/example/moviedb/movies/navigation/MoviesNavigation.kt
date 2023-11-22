package com.example.moviedb.movies.navigation


import com.example.moviedb.movies.ui.movie_details.MovieDetailsRoute
import com.example.moviedb.movies.ui.movies.MoviesRoute
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
    scene(com.example.moviedb.movies.navigation.moviesRoute) {
        MoviesRoute { movieId, title ->
            navigator.navigate("${com.example.moviedb.movies.navigation.movieDetailsRoute}/$movieId/$title", navOptions)
        }
    }
}

fun RouteBuilder.movieDetailsScene(navigator: Navigator) {
    scene("${com.example.moviedb.movies.navigation.movieDetailsRoute}/{${com.example.moviedb.movies.navigation.MOVIE_ID}}/{${com.example.moviedb.movies.navigation.MOVIE_TITLE}}") {
        val movieId: Int = it.path(com.example.moviedb.movies.navigation.MOVIE_ID) ?: 0
        val title: String = it.path(com.example.moviedb.movies.navigation.MOVIE_TITLE) ?: ""
        MovieDetailsRoute(
            movieId = movieId,
            title = title,
            onBackPressed = { navigator.goBack() }
        )
    }
}