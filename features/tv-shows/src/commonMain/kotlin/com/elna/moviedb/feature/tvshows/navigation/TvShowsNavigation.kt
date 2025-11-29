package com.elna.moviedb.feature.tvshows.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen

fun tvShowsEntry(
    key: NavKey,
    backStack: NavBackStack<NavKey>
): NavEntry<NavKey> {
    return NavEntry(key = key) {
        TvShowsScreen(onClick = { tvShowId, _ ->
            backStack.add(TvShowDetailsRoute(tvShowId))
        })
    }
}

fun tvShowDetailsEntry(
    key: TvShowDetailsRoute,
    backStack: NavBackStack<NavKey>
): NavEntry<NavKey> {
    return NavEntry(key = key) {
        TvShowDetailsScreen(
            tvShowId = key.tvShowId,
            onCastMemberClick = { personId ->
                backStack.add(PersonDetailsRoute(personId))
            }
        )
    }
}
