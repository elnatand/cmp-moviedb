package com.example.moviedb.feature.tvshows.navigation


import com.example.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsRoute
import com.example.moviedb.feature.tvshows.ui.tv_shows.TvShowsRoute
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path

const val tvShowsRoute = "/tv_shows"
const val tvShowDetailsRoute = "/tv_show_details"
const val TV_SHOW_ID = "tvShowId"
const val TV_SHOW_TITLE = "tvShowTitle"

fun Navigator.navigateToTvShows(navOptions: NavOptions) {
    navigate(tvShowsRoute, navOptions)
}

fun RouteBuilder.tvShowsScene(navigator: Navigator, navOptions: NavOptions) {
    scene(tvShowsRoute) {
        TvShowsRoute { tvShowId, tvShowTitle ->
            navigator.navigate("$tvShowDetailsRoute/$tvShowId/$tvShowTitle", navOptions)
        }
    }
}

fun RouteBuilder.tvShowDetailsScene(navigator: Navigator) {
    scene("$tvShowDetailsRoute/{$TV_SHOW_ID}/{$TV_SHOW_TITLE}") {
        val tvShowId: Int = it.path(TV_SHOW_ID) ?: 0
        val tvShowTitle: String = it.path(TV_SHOW_TITLE) ?: ""
        TvShowDetailsRoute(
            tvShowId = tvShowId,
            tvShowTitle = tvShowTitle,
            onBackPressed = { navigator.goBack() }
        )
    }
}