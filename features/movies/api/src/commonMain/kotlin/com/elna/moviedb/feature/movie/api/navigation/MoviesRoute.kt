package com.elna.moviedb.feature.movie.api.navigation

import com.elna.moviedb.core.navigation.Route
import kotlinx.serialization.Serializable

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