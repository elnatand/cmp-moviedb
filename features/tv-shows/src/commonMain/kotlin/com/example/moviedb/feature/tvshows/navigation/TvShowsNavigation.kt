package com.example.moviedb.feature.tvshows.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.example.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("tv_shows")
data object TvShowsRoute

@Serializable
@SerialName("tv_show_details")
data class TvShowDetails(
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
            navigator.navigate(TvShowDetails(tvShowId))
        }
    }
}

fun NavGraphBuilder.tvShowDetailsScene(navigator: NavHostController) {
    composable<TvShowDetails> { entry ->
        val params = entry.toRoute<TvShowDetails>()
        val tvShowId: Int = params.tvShowId
        TvShowDetailsScreen(
            tvShowId = tvShowId,
        )
    }
}
