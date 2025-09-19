package com.example.moviedb.feature.tvshows.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsRoute
import com.example.moviedb.feature.tvshows.ui.tv_shows.TvShowsRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("tv_shows")
data object TvShows

@Serializable
@SerialName("tv_show_details")
data class TvShowDetails(
    val tvShowId: Int,
    val title: String
)


const val tvShowsRoute = "/tv_shows"
const val tvShowDetailsRoute = "/tv_show_details"
const val TV_SHOW_ID = "tvShowId"
const val TV_SHOW_TITLE = "tvShowTitle"

fun NavHostController.navigateToTvShows(
    navOptions: NavOptions? = null
) {
    navigate(TvShows, navOptions)
}

fun NavGraphBuilder.tvShowsScene(navigator: NavHostController) {
    composable<TvShows> {
        TvShowsRoute { tvShowId, tvShowTitle ->
            navigator.navigate(TvShowDetails(tvShowId, tvShowTitle))
        }
    }
}

fun NavGraphBuilder.tvShowDetailsScene(navigator: NavHostController) {
    composable<TvShowDetails> { entry ->
        val params = entry.toRoute<TvShowDetails>()
        val tvShowId: Int = params.tvShowId
        val tvShowTitle: String = params.title
        TvShowDetailsRoute(
            tvShowId = tvShowId,
            tvShowTitle = tvShowTitle,
            onBackPressed = { navigator.popBackStack() }
        )
    }
}