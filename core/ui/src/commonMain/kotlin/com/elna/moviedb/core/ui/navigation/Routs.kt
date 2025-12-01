package com.elna.moviedb.core.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
@SerialName("movies")
data object MoviesRoute : Route

@Serializable
@SerialName("movie_details")
data class MovieDetailsRoute(
    val movieId: Int,
) : Route

@Serializable
@SerialName("tv_shows")
data object TvShowsRoute : Route

@Serializable
@SerialName("tv_show_details")
data class TvShowDetailsRoute(
    val tvShowId: Int,
) : Route

@Serializable
@SerialName("search")
data object SearchRoute : Route

@Serializable
@SerialName("profile")
data object ProfileRoute : Route

@Serializable
@SerialName("person_details")
data class PersonDetailsRoute(
    val personId: Int,
) : Route
