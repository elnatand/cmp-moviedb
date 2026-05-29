package com.elna.moviedb.core.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Marked [Serializable] (closed/sealed polymorphism) so the navigation back stack can be
 * persisted and restored across Android process death via a `rememberSaveable` saver. All
 * subtypes below are already [Serializable]; sealed polymorphism needs no SerializersModule,
 * so it works on iOS too.
 */
@Serializable
sealed interface Route


@Serializable
data object MoviesRoute : Route {

    @Serializable
    data object MoviesListRoute : Route

    @Serializable
    data class MovieDetailsRoute(
        val movieId: Int,
        val category: String? = null
    ) : Route
}

@Serializable
data object TvShowsRoute : Route {

    @Serializable
    data object TvShowsListRoute : Route

    @Serializable
    data class TvShowDetailsRoute(
        val tvShowId: Int,
        val category: String? = null
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
