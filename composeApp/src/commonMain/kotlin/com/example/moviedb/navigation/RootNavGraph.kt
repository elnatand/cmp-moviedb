package com.example.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.moviedb.feature.movies.navigation.MoviesRoute
import com.example.moviedb.feature.movies.navigation.movieDetailsScene
import com.example.moviedb.feature.movies.navigation.moviesScene
import com.example.moviedb.feature.profile.navigation.profileScene
import com.example.moviedb.feature.tvshows.navigation.tvShowDetailsScene
import com.example.moviedb.feature.tvshows.navigation.tvShowsScene


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
        profileScene()
    }
}
