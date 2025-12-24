package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen


fun EntryProviderScope<Route>.tvShowsFlow(
    rootBackStack: SnapshotStateList<Route>,
    sharedTransitionScope: SharedTransitionScope
) {
    entry<TvShowsRoute.TvShowsListRoute> {
        TvShowsScreen(
            onClick = { tvShowId: Int, _: String, category: TvShowCategory ->
                rootBackStack.add(TvShowsRoute.TvShowDetailsRoute(tvShowId, category.name))
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedContentScope.current
        )
    }

    entry<TvShowsRoute.TvShowDetailsRoute> {
        TvShowDetailsScreen(
            tvShowId = it.tvShowId,
            category = it.category,
            onCastMemberClick = { personId ->
                rootBackStack.add(PersonDetailsRoute(personId))
            },
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = LocalNavAnimatedContentScope.current
        )
    }
}
