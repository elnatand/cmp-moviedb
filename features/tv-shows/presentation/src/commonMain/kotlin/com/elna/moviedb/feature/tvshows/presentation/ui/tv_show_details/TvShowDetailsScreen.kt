package com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details

import androidx.compose.animation.AnimatedVisibilityScope
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
import com.elna.moviedb.core.ui.design_system.AppBackButton
import com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent
import com.elna.moviedb.feature.tvshows.presentation.ui.components.BasicInfoSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.CastSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.EpisodesSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.GenresSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.HeroSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.LanguagesSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.NetworksSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.OverviewSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.ProductionSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.RatingsSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.SeriesInfoSection
import com.elna.moviedb.feature.tvshows.presentation.ui.components.TrailersSection
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TvShowDetailsScreen(
    tvShowId: Int,
    category: String? = null,
    onBack: () -> Unit,
    onCastMemberClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val viewModel = koinViewModel<com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel> { parametersOf(tvShowId) }
    val uiState by viewModel.uiState.collectAsState()

    _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsScreen(
        uiState = uiState,
        category = category,
        onBack = onBack,
        onRetry = { viewModel.onEvent(_root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.model.TvShowDetailsEvent.Retry) },
        onCastMemberClick = onCastMemberClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}


@Composable
fun TvShowDetailsScreen(
    uiState: com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel.TvShowDetailsUiState,
    category: String? = null,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCastMemberClick: (Int) -> Unit,
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
            is com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel.TvShowDetailsUiState.Loading -> AppLoader()
            is com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel.TvShowDetailsUiState.Error -> AppErrorComponent(
                onRetry = onRetry
            )

            is com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel.TvShowDetailsUiState.Success -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.HeroSection(
                            tvShow = uiState.tvShowDetails,
                            category = category,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.BasicInfoSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.OverviewSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.TrailersSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.CastSection(
                                tvShow = uiState.tvShowDetails,
                                onCastMemberClick = onCastMemberClick
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.RatingsSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.SeriesInfoSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.ProductionSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.EpisodesSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.GenresSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.NetworksSection(
                                tvShow = uiState.tvShowDetails
                            )

                            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.LanguagesSection(
                                tvShow = uiState.tvShowDetails
                            )
                        }
                    }

                    AppBackButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 16.dp, top = 48.dp)
                    )
                }
            }
        }
    }
}
