package com.elna.moviedb.feature.movies.ui.movie_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.movies.model.MovieDetails
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.feature.movies.model.MovieDetailsEvent
import com.elna.moviedb.feature.movies.ui.components.BoxOfficeItem
import com.elna.moviedb.feature.movies.ui.components.CastSection
import com.elna.moviedb.feature.movies.ui.components.InfoItem
import com.elna.moviedb.feature.movies.ui.components.MovieHeroSection
import com.elna.moviedb.feature.movies.ui.components.SectionCard
import com.elna.moviedb.feature.movies.ui.components.TrailersSection
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.box_office
import com.elna.moviedb.resources.budget
import com.elna.moviedb.resources.genres
import com.elna.moviedb.resources.language
import com.elna.moviedb.resources.minutes_suffix
import com.elna.moviedb.resources.overview
import com.elna.moviedb.resources.production_companies
import com.elna.moviedb.resources.production_countries
import com.elna.moviedb.resources.release
import com.elna.moviedb.resources.revenue
import com.elna.moviedb.resources.runtime
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.elna.moviedb.core.ui.design_system.AppBackButton
import com.elna.moviedb.feature.movies.model.MovieDetailsUiState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    category: String? = null,
    onBack: () -> Unit,
    onCastMemberClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val viewModel = koinViewModel<MovieDetailsViewModel> { parametersOf(movieId) }
    val uiState by viewModel.uiState.collectAsState()

    MovieDetailsScreen(
        uiState = uiState,
        category = category,
        onBack = onBack,
        onRetry = { viewModel.onEvent(MovieDetailsEvent.Retry) },
        onCastMemberClick = onCastMemberClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}


@Composable
private fun MovieDetailsScreen(
    uiState: MovieDetailsUiState,
    category: String? = null,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCastMemberClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (uiState) {
            is MovieDetailsUiState.Loading -> AppLoader()

            is MovieDetailsUiState.Error -> AppErrorComponent(
                onRetry = onRetry
            )

            is MovieDetailsUiState.Success -> {
                MovieDetailsContent(
                    movie = uiState.movieDetails,
                    category = category,
                    onBack = onBack,
                    onCastMemberClick = onCastMemberClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}


@Composable
private fun MovieDetailsContent(
    movie: MovieDetails,
    category: String? = null,
    onBack: () -> Unit,
    onCastMemberClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section with Backdrop and Poster
            MovieHeroSection(
                movie = movie,
                category = category,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )

            // Main Content
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Quick Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    movie.releaseDate?.let { date ->
                        InfoItem(
                            icon = Icons.Default.DateRange,
                            label = stringResource(Res.string.release),
                            value = date.take(4)
                        )
                    }

                    movie.runtime?.let { runtime ->
                        InfoItem(
                            icon = Icons.Default.Timer,
                            label = stringResource(Res.string.runtime),
                            value = "$runtime ${stringResource(Res.string.minutes_suffix)}"
                        )
                    }

                    movie.originalLanguage?.let { language ->
                        InfoItem(
                            icon = Icons.Default.Language,
                            label = stringResource(Res.string.language),
                            value = language.uppercase()
                        )
                    }
                }

                // Overview Section
                movie.overview.takeIf { it.isNotBlank() }?.let { overview ->
                    SectionCard(
                        title = stringResource(Res.string.overview),
                        content = {
                            Text(
                                text = overview,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                            )
                        }
                    )
                }

                // Trailers Section
                movie.trailers?.takeIf { it.isNotEmpty() }?.let { trailers ->
                    TrailersSection(trailers = trailers)
                }

                // Cast Section
                CastSection(
                    movie = movie,
                    onCastMemberClick = onCastMemberClick
                )

                // Genres Section
                movie.genres?.takeIf { it.isNotEmpty() }?.let { genres ->
                    SectionCard(
                        title = stringResource(Res.string.genres),
                        content = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                genres.forEach { genre ->
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text(genre) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    )
                }

                // Box Office Section
                if (movie.budget != null || movie.revenue != null) {
                    SectionCard(
                        title = stringResource(Res.string.box_office),
                        content = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                movie.budget?.takeIf { it > 0 }?.let { budget ->
                                    BoxOfficeItem(
                                        label = stringResource(Res.string.budget),
                                        amount = budget
                                    )
                                }
                                movie.revenue?.takeIf { it > 0 }?.let { revenue ->
                                    BoxOfficeItem(
                                        label = stringResource(Res.string.revenue),
                                        amount = revenue
                                    )
                                }
                            }
                        }
                    )
                }

                // Production Section
                movie.productionCompanies?.takeIf { it.isNotEmpty() }?.let { companies ->
                    SectionCard(
                        title = stringResource(Res.string.production_companies),
                        content = {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                companies.forEach { company ->
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text(company) }
                                    )
                                }
                            }
                        }
                    )
                }

                // Countries Section
                movie.productionCountries?.takeIf { it.isNotEmpty() }?.let { countries ->
                    SectionCard(
                        title = stringResource(Res.string.production_countries),
                        content = {
                            Text(
                                text = countries.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
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

@Preview
@Composable
private fun MovieDetailsScreenSuccessPreview() {
    val sampleMovie = MovieDetails(
        id = 1,
        title = "Sample Movie",
        overview = "This is a sample movie overview that demonstrates how the movie details screen looks with content.",
        posterPath = "/sample_poster.jpg",
        backdropPath = "/sample_backdrop.jpg",
        releaseDate = "2023-12-01",
        voteAverage = 8.5,
        voteCount = 1250,
        runtime = 142,
        tagline = "An amazing cinematic experience",
        genres = listOf("Action", "Adventure", "Sci-Fi"),
        originalLanguage = "en",
        budget = 150000000,
        revenue = 750000000,
        productionCompanies = listOf("Marvel Studios", "Disney"),
        productionCountries = listOf("United States", "United Kingdom"),
        adult = false,
        homepage = "https://example.com",
        imdbId = "tt1234567",
        originalTitle = "Sample Movie",
        popularity = 85.5,
        status = "Released",
        spokenLanguages = listOf("English", "Spanish")
    )

    MovieDetailsScreen(
        uiState = MovieDetailsUiState.Success(sampleMovie),
        onBack = {},
        onRetry = {},
        onCastMemberClick = {}
    )
}

@Preview
@Composable
private fun MovieDetailsScreenLoadingPreview() {
    MovieDetailsScreen(
        uiState = MovieDetailsUiState.Loading,
        onBack = {},
        onRetry = {},
        onCastMemberClick = {}
    )
}

@Preview
@Composable
private fun MovieDetailsScreenErrorPreview() {
    MovieDetailsScreen(
        uiState = MovieDetailsUiState.Error("Failed to load movie details. Please check your internet connection."),
        onBack = {},
        onRetry = {},
        onCastMemberClick = {}
    )
}
