package com.example.moviedb.feature.movies.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.example.moviedb.feature.movies.ui.movies.MoviesRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("movies")
data object MoviesRoute

@Serializable
@SerialName("movie_details")
data class MovieDetailsRoute(
    val movieId: Int,
)

fun NavHostController.navigateToMovies(navOptions: NavOptions) {
    navigate(MoviesRoute, navOptions)
}


fun NavGraphBuilder.moviesScene(
    navigator: NavHostController,
) {
    composable<MoviesRoute> { entry ->
        MoviesRoute { movieId, title ->
            navigator.navigate(MovieDetailsRoute(movieId))
        }
    }
}

fun NavGraphBuilder.movieDetailsScene(navigator: NavHostController) {
    composable<MovieDetailsRoute> { entry ->
        val params = entry.toRoute<MovieDetailsRoute>()

        val movieId: Int = params.movieId

        MovieDetailsScreen(
            movieId = movieId,
        )
    }
}