package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.movies.navigation.movieDetailsEntry
import com.elna.moviedb.feature.movies.navigation.moviesEntry
import com.elna.moviedb.feature.person.navigation.personDetailsEntry
import com.elna.moviedb.feature.profile.navigation.profileEntry
import com.elna.moviedb.feature.search.navigation.searchEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowDetailsEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowsEntry


@Composable
fun RootNavGraph(
    backStack: NavBackStack<NavKey>,
) {
    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                MoviesRoute -> moviesEntry(key, backStack)

                is MovieDetailsRoute -> movieDetailsEntry(key, backStack)

                TvShowsRoute -> tvShowsEntry(key, backStack)

                is TvShowDetailsRoute -> tvShowDetailsEntry(key, backStack)

                SearchRoute -> searchEntry(key, backStack)

                ProfileRoute -> profileEntry(key)

                is PersonDetailsRoute -> personDetailsEntry(key, backStack)

                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }
    )
}
