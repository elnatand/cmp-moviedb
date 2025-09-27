package com.elna.moviedb.feature.tvshows.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("tv_shows")
data object TvShowsRoute

@Serializable
@SerialName("tv_show_details")
data class TvShowDetailsRoute(
    val tvShowId: Int,
)

fun NavHostController.navigateToTvShows(
    navOptions: NavOptions? = null
) {
    navigate(TvShowsRoute, navOptions)
}

fun NavGraphBuilder.tvShowsScene(navigator: NavHostController) {
    composable<TvShowsRoute> {
        TvShowsScreen { tvShowId, tvShowTitle ->
            navigator.navigate(TvShowDetailsRoute(tvShowId))
        }
    }
}

fun NavGraphBuilder.tvShowDetailsScene(navigator: NavHostController) {
    composable<TvShowDetailsRoute> { entry ->
        val params = entry.toRoute<TvShowDetailsRoute>()
        val tvShowId: Int = params.tvShowId
        TvShowDetailsScreen(
            tvShowId = tvShowId,
        )
    }
}
