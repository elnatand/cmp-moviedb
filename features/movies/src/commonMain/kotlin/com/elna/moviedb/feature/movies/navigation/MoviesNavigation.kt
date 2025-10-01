package com.elna.moviedb.feature.movies.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen


fun NavHostController.navigateToMovies(navOptions: NavOptions) {
    navigate(MoviesRoute, navOptions)
}


fun NavGraphBuilder.moviesScene(
    navigator: NavHostController,
) {
    composable<MoviesRoute> { entry ->
        MoviesScreen { movieId, title ->
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