package com.elna.moviedb.core.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route


@Serializable
data class MoviesRoute(val startAt: Route = MoviesListRoute) : Route {

    @Serializable
    data object MoviesListRoute : Route

    @Serializable
    data class MovieDetailsRoute(
        val movieId: Int,
        val category: String? = null,
    ) : Route
}

@Serializable
data class TvShowsRoute(val startAt: Route = TvShowsListRoute) : Route {

    @Serializable
    data object TvShowsListRoute : Route

    @Serializable
    data class TvShowDetailsRoute(
        val tvShowId: Int,
        val category: String? = null,
    ) : Route
}

@Serializable
data object SearchRoute : Route

@Serializable
data object ProfileRoute : Route

@Serializable
data class PersonDetailsRoute(
    val personId: Int,
) : Route
