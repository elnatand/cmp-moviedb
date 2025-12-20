package com.elna.moviedb.feature.tvshows.ui.tv_show_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.tvshows.model.TvShowDetailsEvent
import com.elna.moviedb.feature.tvshows.ui.components.BasicInfoSection
import com.elna.moviedb.feature.tvshows.ui.components.CastSection
import com.elna.moviedb.feature.tvshows.ui.components.EpisodesSection
import com.elna.moviedb.feature.tvshows.ui.components.GenresSection
import com.elna.moviedb.feature.tvshows.ui.components.HeroSection
import com.elna.moviedb.feature.tvshows.ui.components.LanguagesSection
import com.elna.moviedb.feature.tvshows.ui.components.NetworksSection
import com.elna.moviedb.feature.tvshows.ui.components.OverviewSection
import com.elna.moviedb.feature.tvshows.ui.components.ProductionSection
import com.elna.moviedb.feature.tvshows.ui.components.RatingsSection
import com.elna.moviedb.feature.tvshows.ui.components.SeriesInfoSection
import com.elna.moviedb.feature.tvshows.ui.components.TrailersSection
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowDetailsScreen(
    tvShowId: Int,
    onCastMemberClick: (Int) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val viewModel = koinViewModel<TvShowDetailsViewModel> { parametersOf(tvShowId) }
    val uiState by viewModel.uiState.collectAsState()

    TvShowDetailsScreen(
        uiState = uiState,
        onRetry = { viewModel.onEvent(TvShowDetailsEvent.Retry) },
        onCastMemberClick = onCastMemberClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TvShowDetailsScreen(
    uiState: TvShowDetailsViewModel.TvShowDetailsUiState,
    onRetry: () -> Unit,
    onCastMemberClick: (Int) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is TvShowDetailsViewModel.TvShowDetailsUiState.Loading -> AppLoader()
            is TvShowDetailsViewModel.TvShowDetailsUiState.Error -> AppErrorComponent(
                onRetry = onRetry
            )

            is TvShowDetailsViewModel.TvShowDetailsUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    HeroSection(
                        tvShow = uiState.tvShowDetails,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        BasicInfoSection(tvShow = uiState.tvShowDetails)

                        OverviewSection(tvShow = uiState.tvShowDetails)

                        TrailersSection(tvShow = uiState.tvShowDetails)

                        CastSection(
                            tvShow = uiState.tvShowDetails,
                            onCastMemberClick = onCastMemberClick
                        )

                        RatingsSection(tvShow = uiState.tvShowDetails)

                        SeriesInfoSection(tvShow = uiState.tvShowDetails)

                        ProductionSection(tvShow = uiState.tvShowDetails)

                        EpisodesSection(tvShow = uiState.tvShowDetails)

                        GenresSection(tvShow = uiState.tvShowDetails)

                        NetworksSection(tvShow = uiState.tvShowDetails)

                        LanguagesSection(tvShow = uiState.tvShowDetails)
                    }
                }
            }
        }
    }
}
