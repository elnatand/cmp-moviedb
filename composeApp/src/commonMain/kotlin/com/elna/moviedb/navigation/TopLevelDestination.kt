package com.elna.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute

enum class TopLevelDestination(
    val icon: ImageVector,
    val route: Any,
) {
    MOVIES(
        icon = Icons.Filled.Theaters,
        route = MoviesRoute
    ),
    TV_SHOWS(
        icon = Icons.Filled.Tv,
        route = TvShowsRoute
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