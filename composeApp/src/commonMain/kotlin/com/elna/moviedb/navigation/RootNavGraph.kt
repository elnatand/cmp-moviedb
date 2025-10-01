package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.elna.moviedb.feature.movies.navigation.MoviesRoute
import com.elna.moviedb.feature.movies.navigation.movieDetailsScene
import com.elna.moviedb.feature.movies.navigation.moviesScene
import com.elna.moviedb.feature.profile.navigation.profileScene
import com.elna.moviedb.feature.search.navigation.searchScene
import com.elna.moviedb.feature.tvshows.navigation.tvShowDetailsScene
import com.elna.moviedb.feature.tvshows.navigation.tvShowsScene


@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: Any = MoviesRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        moviesScene(navController)
        movieDetailsScene(navController)
        tvShowsScene(navController)
        tvShowDetailsScene(navController)
        searchScene(navController)
        profileScene()
    }
}
