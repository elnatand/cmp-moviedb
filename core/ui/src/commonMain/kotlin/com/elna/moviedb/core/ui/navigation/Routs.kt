package com.elna.moviedb.core.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("movies")
data object MoviesRoute

@Serializable
@SerialName("movie_details")
data class MovieDetailsRoute(
    val movieId: Int,
)

@Serializable
@SerialName("tv_shows")
data object TvShowsRoute

@Serializable
@SerialName("tv_show_details")
data class TvShowDetailsRoute(
    val tvShowId: Int,
)

@Serializable
@SerialName("search")
object SearchRoute

@Serializable
@SerialName("profile")
data object ProfileRoute

@Serializable
@SerialName("person_details")
data class PersonDetailsRoute(
    val personId: Int,
)
