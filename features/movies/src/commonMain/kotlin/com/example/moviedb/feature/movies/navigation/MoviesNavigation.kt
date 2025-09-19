package com.example.moviedb.feature.movies.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsRoute
import com.example.moviedb.feature.movies.ui.movies.MoviesRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


const val movieDetailsRoute = "/movie_details"
const val MOVIE_ID = "movieId"
const val MOVIE_TITLE = "movie_title"

@Serializable
@SerialName("movies")
data object Movies

@Serializable
@SerialName("movie_details")
data class MovieDetails(
    val movieId: Int,
    val title: String
)

fun NavHostController.navigateToMovies() {
    navigate(Movies)
}


fun NavGraphBuilder.moviesScene(
    navigator: NavHostController,
) {
    composable<Movies> { entry ->
        MoviesRoute { movieId, title ->
            navigator.navigate(MovieDetails(movieId, title))
        }
    }
}

fun NavGraphBuilder.movieDetailsScene(navigator: NavHostController) {
    composable<MovieDetails> { entry ->
        val params = entry.toRoute<MovieDetails>()

        val movieId: Int = params.movieId
        val title: String = params.title
        MovieDetailsRoute(
            movieId = movieId,
            title = title,
            onBackPressed = { navigator.popBackStack() }
        )
    }
}