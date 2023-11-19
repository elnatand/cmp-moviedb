package com.example.moviedb.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moviedb.features.movies.navigation.moviesRoute
import com.example.moviedb.features.profile.navigation.profileRoute
import com.example.moviedb.features.tv_shows.navigation.tvShowsRoute
import com.example.moviedb.ui.strings.Strings


enum class TopLevelDestination(
    val icon: ImageVector,
    val titleRes: Strings,
    val route: String
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        titleRes = Strings.movies,
        route = moviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        titleRes = Strings.tv_shows,
        route = tvShowsRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        titleRes = Strings.profile,
        route = profileRoute
    ),
}