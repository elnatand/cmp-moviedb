package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.MovieDetailsRoute
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowDetailsRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen
import com.elna.moviedb.feature.person.ui.PersonDetailsScreen
import com.elna.moviedb.feature.profile.ui.ProfileScreen
import com.elna.moviedb.feature.search.ui.SearchScreen
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsScreen


@Composable
fun RootNavGraph(
    backStack: NavBackStack<NavKey>,
    startDestination: Any = MoviesRoute,
) {

//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//    ) {
//        moviesScene(navController)
//        movieDetailsScene(navController)
//        tvShowsScene(navController)
//        tvShowDetailsScene(navController)
//        searchScene(navController)
//        profileScene()
//        personDetailsScene(navController)
//    }


    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                MoviesRoute -> {
                    NavEntry(key = key) {
                        MoviesScreen(onClick = { movieId, _ ->
                            backStack.add(MovieDetailsRoute(movieId))
                        })
                    }
                }

                is MovieDetailsRoute -> {
                    NavEntry(key = key) {
                        MovieDetailsScreen(
                            movieId = key.movieId,
                            onCastMemberClick = { castMember ->
                                backStack.add(PersonDetailsRoute(castMember))
                            }
                        )
                    }
                }

                TvShowsRoute -> {
                    NavEntry(key = key) {
                        TvShowsScreen(onClick = { movieId, _ ->
                            backStack.add(TvShowDetailsRoute(movieId))
                        })
                    }
                }

                is TvShowDetailsRoute -> {
                    NavEntry(key = key) {
                        TvShowDetailsScreen(
                            tvShowId = key.tvShowId,
                            onCastMemberClick = { castMember ->
                                backStack.add(PersonDetailsRoute(castMember))
                            },
                        )
                    }
                }

                is SearchRoute -> {
                    NavEntry(key = key) {
                        SearchScreen(
                            onMovieClicked = { backStack.add(MovieDetailsRoute(it)) },
                            onTvShowClicked = { backStack.add(TvShowDetailsRoute(it)) },
                            onPersonClicked = { backStack.add(PersonDetailsRoute(it)) })
                    }
                }

                ProfileRoute -> {
                    NavEntry(key = key) { ProfileScreen() }
                }

                is PersonDetailsRoute -> {
                    NavEntry(key = key) {
                        PersonDetailsScreen(
                            personId = key.personId,
                            onCreditClick = { id, mediaType ->
                                when (mediaType) {
                                    MediaType.MOVIE -> backStack.add(MovieDetailsRoute(id))
                                    MediaType.TV -> backStack.add(TvShowDetailsRoute(id))
                                }
                            }
                        )
                    }
                }

                else -> {
                    throw IllegalArgumentException("Invalid key: $key")
                }
            }
        }
    )
}
