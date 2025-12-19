package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TVShowsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

fun EntryProviderScope<Route>.tvShowsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<TVShowsRoute.TvShowsListRoute> {
        TvShowsScreen(onClick = { tvShowId, _ ->
            backStack.add(TVShowsRoute.TvShowDetailsRoute(tvShowId))
        })
    }
}

fun EntryProviderScope<Route>.tvShowDetailsEntry(
    onCastMemberClick: (personId: Int) -> Unit
) {
    entry<TVShowsRoute.TvShowDetailsRoute> {
        TvShowDetailsScreen(
            tvShowId = it.tvShowId,
            onCastMemberClick = onCastMemberClick
        )
    }
}
