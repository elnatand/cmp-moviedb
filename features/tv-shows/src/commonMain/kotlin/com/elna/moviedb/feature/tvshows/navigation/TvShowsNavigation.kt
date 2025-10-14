package com.elna.moviedb.feature.tvshows.navigation


import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen


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
            onCastMemberClick = { personId ->
                navigator.navigate(PersonDetailsRoute(personId))
            }
        )
    }
}
