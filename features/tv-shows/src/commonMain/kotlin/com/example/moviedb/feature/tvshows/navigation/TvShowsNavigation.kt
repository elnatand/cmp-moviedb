package com.example.moviedb.feature.tvshows.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsRoute
import com.example.moviedb.feature.tvshows.ui.tv_shows.TvShowsRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("tv_shows")
data object TV_SHOWS


const val tvShowsRoute = "/tv_shows"
const val tvShowDetailsRoute = "/tv_show_details"
const val TV_SHOW_ID = "tvShowId"
const val TV_SHOW_TITLE = "tvShowTitle"

fun NavHostController.navigateToTvShows(
    navOptions: NavOptions? = null
) {
    navigate(TV_SHOWS, navOptions)
}

fun NavGraphBuilder.tvShowsScene(navigator: NavHostController) {
    composable<TV_SHOWS> {
        TvShowsRoute { tvShowId, tvShowTitle ->
           // navigator.navigate("$tvShowDetailsRoute/$tvShowId/$tvShowTitle", )
        }
    }
}

//fun RouteBuilder.tvShowDetailsScene(navigator: Navigator) {
//    scene("$tvShowDetailsRoute/{$TV_SHOW_ID}/{$TV_SHOW_TITLE}") {
//        val tvShowId: Int = it.path(TV_SHOW_ID) ?: 0
//        val tvShowTitle: String = it.path(TV_SHOW_TITLE) ?: ""
//        TvShowDetailsRoute(
//            tvShowId = tvShowId,
//            tvShowTitle = tvShowTitle,
//            onBackPressed = { navigator.goBack() }
//        )
//    }
//}