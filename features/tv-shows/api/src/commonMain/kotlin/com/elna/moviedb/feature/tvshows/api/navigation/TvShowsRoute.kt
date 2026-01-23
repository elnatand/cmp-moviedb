package com.elna.moviedb.feature.tvshows.api.navigation

import com.elna.moviedb.core.navigation.Route
import kotlinx.serialization.Serializable

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
