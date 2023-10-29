package features.tv_shows.navigation


import features.tv_shows.ui.tv_show_details.TvShowDetailsRoute
import features.tv_shows.ui.tv_shows.TvShowsRoute
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path

const val tvShowsRoute = "/tv_shows"
const val tvShowDetailsRoute = "/tv_show_details"
const val TV_SHOW_ID = "tvShowId"

fun Navigator.navigateToTvShows(navOptions: NavOptions){
    navigate(tvShowsRoute, navOptions)
}

fun RouteBuilder.tvShowsScene(navigator: Navigator, navOptions: NavOptions) {
    scene(tvShowsRoute) {
        TvShowsRoute {
            navigator.navigate("${tvShowDetailsRoute}/$it", navOptions)
        }
    }
}

fun RouteBuilder.tvShowDetailsScene(navigator: Navigator) {
    scene("${tvShowDetailsRoute}/{$TV_SHOW_ID}") {
        val tvShowId: Int? = it.path(TV_SHOW_ID)
        TvShowDetailsRoute(
            tvShowId = tvShowId,
            onBackPressed = { navigator.goBack() }
        )
    }
}