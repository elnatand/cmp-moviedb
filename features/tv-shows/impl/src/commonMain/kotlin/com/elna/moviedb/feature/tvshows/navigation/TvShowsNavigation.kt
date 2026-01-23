package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.core.navigation.Route
import com.elna.moviedb.feature.person.api.navigation.PersonDetailsRoute
import com.elna.moviedb.feature.tvshows.api.navigation.TvShowsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

/**
 * A [NavigationFactory] that defines the navigation graph for the TV shows feature.
 *
 * This factory is responsible for creating and configuring the navigation entries for the
 * TV shows list screen and the TV show details screen. It handles navigation actions
 * such as navigating to a TV show's details, navigating to a cast member's details,
 * and handling the back action.
 */
class TvShowsNavigationFactory : NavigationFactory {
    override fun create(
        builder: EntryProviderScope<Route>,
        rootBackStack: SnapshotStateList<Route>,
        sharedTransitionScope: SharedTransitionScope
    ) = with(builder) {
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
                onBack = { rootBackStack.removeLastOrNull() },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                onCastMemberClick = { personId ->
                    rootBackStack.add(PersonDetailsRoute(personId))
                }
            )
        }
    }
}
