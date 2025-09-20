package com.example.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moviedb.feature.movies.navigation.MoviesRoute
import com.example.moviedb.feature.profile.navigation.ProfileRoute
import com.example.moviedb.feature.tvshows.navigation.TvShowsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val icon: ImageVector,
    val route: KClass<*>,
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        route = MoviesRoute::class
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        route = TvShowsRoute::class
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        route = ProfileRoute::class
    ),
}