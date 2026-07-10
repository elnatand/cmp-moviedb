package com.elna.moviedb.feature.tvshows.presentation.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.feature.tvshows.domain.model.TvShowCategory
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows.TvShowsScreen


fun EntryProviderScope<Route>.tvShowsFlow(
    navigator: Navigator,
    sharedTransitionScope: SharedTransitionScope?
) {
    entry<TvShowsRoute.TvShowsListRoute> {
        TvShowsScreen(
            onClick = { tvShowId: Int, title: String, category: TvShowCategory ->
                navigator.navigate(TvShowsRoute.TvShowDetailsRoute(tvShowId, category.name), title)
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = if (sharedTransitionScope != null) LocalNavAnimatedContentScope.current else null
        )
    }

    entry<TvShowsRoute.TvShowDetailsRoute> {
        TvShowDetailsScreen(
            tvShowId = it.tvShowId,
            category = it.category,
            onBack = navigator::goBack,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = if (sharedTransitionScope != null) LocalNavAnimatedContentScope.current else null,
            onCastMemberClick = { personId ->
                navigator.navigate(PersonDetailsRoute(personId))
            }
        )
    }
}
