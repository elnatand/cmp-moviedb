package com.elna.moviedb.feature.search.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.elna.moviedb.feature.search.ui.SearchScreen
import com.elna.moviedb.feature.search.ui.SearchViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
@SerialName("search")
object SearchRoute

fun NavHostController.navigateToSearch(navOptions: NavOptions) {
    navigate(SearchRoute, navOptions)
}

fun NavGraphBuilder.searchScene(navigator: NavHostController) {
    composable<SearchRoute> {
        val viewModel = koinViewModel<SearchViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        SearchScreen(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onFilterChanged = viewModel::onFilterChanged,
            onRetry = viewModel::onRetry,
            onMovieClicked = { movieId ->
            //    navigator.navigate(MovieDetailsRoute(movieId))
            },
            onTvShowClicked = { tvShowId ->
            //    navigator.navigate(TvShowDetailsRoute(tvShowId))
            }
        )
    }
}