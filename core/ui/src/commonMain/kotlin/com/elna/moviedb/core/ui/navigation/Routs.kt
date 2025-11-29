package com.elna.moviedb.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("movies")
data object MoviesRoute : NavKey

@Serializable
@SerialName("movie_details")
data class MovieDetailsRoute(
    val movieId: Int,
) : NavKey

@Serializable
@SerialName("tv_shows")
data object TvShowsRoute : NavKey

@Serializable
@SerialName("tv_show_details")
data class TvShowDetailsRoute(
    val tvShowId: Int,
) : NavKey

@Serializable
@SerialName("search")
data object SearchRoute : NavKey

@Serializable
@SerialName("profile")
data object ProfileRoute : NavKey

@Serializable
@SerialName("person_details")
data class PersonDetailsRoute(
    val personId: Int,
) : NavKey
