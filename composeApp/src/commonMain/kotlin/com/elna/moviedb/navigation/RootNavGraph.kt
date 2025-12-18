package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.movies.navigation.movieDetailsEntry
import com.elna.moviedb.feature.movies.navigation.moviesEntry
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen
import com.elna.moviedb.feature.person.navigation.personDetailsEntry
import com.elna.moviedb.feature.profile.navigation.profileEntry
import com.elna.moviedb.feature.search.navigation.searchEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowDetailsEntry
import com.elna.moviedb.feature.tvshows.navigation.tvShowsEntry


@Composable
fun RootNavGraph(
    backStack: SnapshotStateList<Route>,
) {
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            moviesEntry(backStack)
            movieDetailsEntry(backStack)
        }
//        entryProvider = { key ->
//            when (key) {
//                MoviesRoute -> moviesEntry(key, backStack)
//                is MovieDetailsRoute -> movieDetailsEntry(key, backStack)
//                TvShowsRoute -> tvShowsEntry(key, backStack)
//                is TvShowDetailsRoute -> tvShowDetailsEntry(key, backStack)
//                SearchRoute -> searchEntry(key, backStack)
//                ProfileRoute -> profileEntry(key)
//                is PersonDetailsRoute -> personDetailsEntry(key, backStack)
//            }
//        }
    )
}


