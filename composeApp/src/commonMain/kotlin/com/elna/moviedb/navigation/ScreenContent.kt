package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import com.elna.moviedb.core.ui.navigation.MoviesRoute
import com.elna.moviedb.core.ui.navigation.PersonDetailsRoute
import com.elna.moviedb.core.ui.navigation.ProfileRoute
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.core.ui.navigation.SearchRoute
import com.elna.moviedb.core.ui.navigation.TvShowsRoute
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsScreen
import com.elna.moviedb.feature.movies.ui.movies.MoviesScreen
import com.elna.moviedb.feature.person.domain.model.MediaType
import com.elna.moviedb.feature.person.presentation.ui.PersonDetailsScreen
import com.elna.moviedb.feature.profile.presentation.ui.ProfileScreen
import com.elna.moviedb.feature.search.presentation.ui.SearchScreen
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsScreen
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows.TvShowsScreen

/**
 * Renders a single [route] as a standalone screen, outside any [androidx.navigation3.ui.NavDisplay].
 *
 * Used by the iOS 26+ Liquid Glass shell, where SwiftUI's NavigationStack owns the back stack:
 * each screen lives in its own ComposeUIViewController, so navigation is delegated to the host
 * via [onNavigate] (push) and [onBack] (pop) instead of mutating a Compose back stack.
 *
 * [onNavigate] also carries a display title (empty when unknown) for the native toolbar.
 * Shared-element transitions are skipped — screens in separate view controllers can't share
 * a transition scope; the native push/pop animation takes their place.
 */
@Composable
fun ScreenContent(
    route: Route,
    onNavigate: (route: Route, title: String) -> Unit,
    onBack: () -> Unit,
) {
    when (route) {
        MoviesRoute.MoviesListRoute -> MoviesScreen(
            onClick = { movieId, title, category ->
                onNavigate(MoviesRoute.MovieDetailsRoute(movieId, category.name), title)
            }
        )

        is MoviesRoute.MovieDetailsRoute -> MovieDetailsScreen(
            movieId = route.movieId,
            category = route.category,
            onBack = onBack,
            onCastMemberClick = { personId ->
                onNavigate(PersonDetailsRoute(personId), "")
            }
        )

        TvShowsRoute.TvShowsListRoute -> TvShowsScreen(
            onClick = { tvShowId, title, category ->
                onNavigate(TvShowsRoute.TvShowDetailsRoute(tvShowId, category.name), title)
            }
        )

        is TvShowsRoute.TvShowDetailsRoute -> TvShowDetailsScreen(
            tvShowId = route.tvShowId,
            category = route.category,
            onBack = onBack,
            onCastMemberClick = { personId ->
                onNavigate(PersonDetailsRoute(personId), "")
            }
        )

        SearchRoute -> SearchScreen(
            onMovieClicked = { movieId ->
                onNavigate(MoviesRoute.MovieDetailsRoute(movieId), "")
            },
            onTvShowClicked = { tvShowId ->
                onNavigate(TvShowsRoute.TvShowDetailsRoute(tvShowId), "")
            },
            onPersonClicked = { personId ->
                onNavigate(PersonDetailsRoute(personId), "")
            }
        )

        is PersonDetailsRoute -> PersonDetailsScreen(
            personId = route.personId,
            onBack = onBack,
            onCreditClick = { id, mediaType ->
                when (mediaType) {
                    MediaType.MOVIE -> onNavigate(MoviesRoute.MovieDetailsRoute(id), "")
                    MediaType.TV -> onNavigate(TvShowsRoute.TvShowDetailsRoute(id), "")
                }
            }
        )

        ProfileRoute -> ProfileScreen()

        // Grouping parents, never navigated to directly.
        MoviesRoute, TvShowsRoute -> Unit
    }
}
