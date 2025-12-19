package com.elna.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.elna.moviedb.core.ui.navigation.MoviesListRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsListRoute

enum class TopLevelDestination(
    val icon: ImageVector,
    val route: Route,
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        route = MoviesListRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        route = TvShowsListRoute
    ),
    SEARCH(
        icon = Icons.Filled.Search,
        route = SearchRoute
    ),
    PROFILE(
        icon = Icons.Filled.Person,
        route = ProfileRoute
    ),
}