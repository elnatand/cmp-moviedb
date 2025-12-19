package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowsListRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

fun EntryProviderScope<Route>.tvShowsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<TvShowsListRoute> {
        TvShowsScreen(onClick = { tvShowId, _ ->
            backStack.add(TvShowDetailsRoute(tvShowId))
        })
    }
}

fun EntryProviderScope<Route>.tvShowDetailsEntry(
    backStack: SnapshotStateList<Route>
) {
    entry<TvShowDetailsRoute> {
        TvShowDetailsScreen(
            tvShowId = it.tvShowId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
