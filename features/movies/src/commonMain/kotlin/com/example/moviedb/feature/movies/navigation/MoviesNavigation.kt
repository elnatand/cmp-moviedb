package com.example.moviedb.feature.movies.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsRoute
import com.example.moviedb.feature.movies.ui.movies.MoviesRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


const val movieDetailsRoute = "/movie_details"
const val MOVIE_ID = "movieId"
const val MOVIE_TITLE = "movie_title"

@Serializable
@SerialName("movies")
data object MOVIES

fun NavHostController.navigateToMovies() {
    navigate(MOVIES)
}

fun NavGraphBuilder.moviesScene(navigator: NavHostController) {
    composable<MOVIES> { entry ->
        MoviesRoute { movieId, title ->

        }
    }
}

fun NavGraphBuilder.movieDetailsScene(navigator: NavHostController) {
//    scene("$movieDetailsRoute/{$MOVIE_ID}/{$MOVIE_TITLE}") {
//        val movieId: Int = it.path(MOVIE_ID) ?: 0
//        val title: String = it.path(MOVIE_TITLE) ?: ""
//        MovieDetailsRoute(
//            movieId = movieId,
//            title = title,
//            onBackPressed = { navigator.goBack() }
//        )
//    }
}