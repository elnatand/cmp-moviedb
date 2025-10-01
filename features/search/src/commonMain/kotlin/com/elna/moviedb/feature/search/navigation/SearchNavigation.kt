package com.elna.moviedb.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.search.ui.SearchScreen


fun NavHostController.navigateToSearch(navOptions: NavOptions) {
    navigate(SearchRoute, navOptions)
}

fun NavGraphBuilder.searchScene(navigator: NavHostController) {
    composable<SearchRoute> {
        SearchScreen(
            onMovieClicked = { movieId ->
                navigator.navigate(MovieDetailsRoute(movieId))
            },
            onTvShowClicked = { tvShowId ->
               navigator.navigate(TvShowDetailsRoute(tvShowId))
            }
        )
    }
}