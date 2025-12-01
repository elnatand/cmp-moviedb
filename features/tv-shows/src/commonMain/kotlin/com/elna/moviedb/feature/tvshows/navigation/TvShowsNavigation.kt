package com.elna.moviedb.feature.tvshows.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

fun tvShowsEntry(
    key: Route,
    backStack: SnapshotStateList<Route>
): NavEntry<Route> {
    return NavEntry(key = key) {
        TvShowsScreen(onClick = { tvShowId, _ ->
            backStack.add(TvShowDetailsRoute(tvShowId))
        })
    }
}

fun tvShowDetailsEntry(
    key: TvShowDetailsRoute,
    backStack: SnapshotStateList<Route>
): NavEntry<Route> {
    return NavEntry(key = key) {
        TvShowDetailsScreen(
            tvShowId = key.tvShowId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
