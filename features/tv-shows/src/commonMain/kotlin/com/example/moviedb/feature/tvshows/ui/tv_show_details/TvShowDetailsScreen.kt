package com.example.moviedb.feature.tvshows.ui.tv_show_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import com.example.moviedb.core.model.TvShowDetails
import com.example.moviedb.core.ui.design_system.AppErrorComponent
import com.example.moviedb.core.ui.design_system.AppLoader
import com.example.moviedb.core.ui.utils.ImageLoader
import org.jetbrains.compose.resources.stringResource
import com.example.moviedb.resources.Res
import com.example.moviedb.resources.overview
import com.example.moviedb.resources.genres
import com.example.moviedb.resources.unknown
import com.example.moviedb.resources.series_information
import com.example.moviedb.resources.first_air_date
import com.example.moviedb.resources.last_air_date
import com.example.moviedb.resources.seasons
import com.example.moviedb.resources.episodes
import com.example.moviedb.resources.networks
import com.example.moviedb.resources.languages
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private fun formatDate(dateString: String): String {
    if (dateString.isEmpty()) return ""
    return try {
        // Input format: yyyy-mm-dd
        val parts = dateString.split("-")
        if (parts.size == 3) {
            "${parts[2]}.${parts[1]}.${parts[0]}"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun TvShowDetailsScreen(
    tvShowId: Int,
) {
    val viewModel = koinViewModel<TvShowDetailsViewModel> { parametersOf(tvShowId) }
    val uiState by viewModel.uiState.collectAsState()

    TvShowDetailsScreen(
        uiState = uiState,
        onRetry = viewModel::retry
    )
}


@Composable
fun TvShowDetailsScreen(
    uiState: TvShowDetailsViewModel.TvShowDetailsUiState,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is TvShowDetailsViewModel.TvShowDetailsUiState.Loading -> AppLoader()
            is TvShowDetailsViewModel.TvShowDetailsUiState.Error -> AppErrorComponent(
                message = uiState.message,
                onRetry = onRetry
            )

            is TvShowDetailsViewModel.TvShowDetailsUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero Section with Backdrop and Poster
                    HeroSection(tvShow = uiState.tvShowDetails)

                    // Content Section
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Basic Info Section
                        BasicInfoSection(tvShow = uiState.tvShowDetails)

                        // Overview Section
                        OverviewSection(tvShow = uiState.tvShowDetails)

                        // Ratings and Stats Section
                        RatingsSection(tvShow = uiState.tvShowDetails)

                        // Series Information Section
                        SeriesInfoSection(tvShow = uiState.tvShowDetails)

                        // Production Section
                        ProductionSection(tvShow = uiState.tvShowDetails)

                        // Episodes Section
                        EpisodesSection(tvShow = uiState.tvShowDetails)

                        // Genres Section
                        GenresSection(tvShow = uiState.tvShowDetails)

                        // Networks Section
                        NetworksSection(tvShow = uiState.tvShowDetails)

                        // Languages Section
                        LanguagesSection(tvShow = uiState.tvShowDetails)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSection(tvShow: TvShowDetails) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Backdrop Image
        if (tvShow.backdropPath.isNotEmpty()) {
            ImageLoader(
                imageUrl = "$TMDB_IMAGE_URL${tvShow.backdropPath}",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Poster and Title
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Poster
            Card(
                modifier = Modifier.size(width = 120.dp, height = 180.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                ImageLoader(
                    imageUrl = "$TMDB_IMAGE_URL${tvShow.posterPath}",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Title and Basic Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tvShow.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (tvShow.originalName != tvShow.name) {
                    Text(
                        text = tvShow.originalName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${(tvShow.voteAverage * 10).toInt() / 10.0}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    // Status
                    if (tvShow.status.isNotEmpty()) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tvShow.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BasicInfoSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.series_information),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = stringResource(Res.string.first_air_date),
                value = formatDate(tvShow.firstAirDate).ifEmpty { stringResource(Res.string.unknown) }
            )

            if (tvShow.lastAirDate.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(Res.string.last_air_date),
                    value = formatDate(tvShow.lastAirDate)
                )
            }

            InfoRow(
                icon = Icons.Default.Tv,
                label = stringResource(Res.string.seasons),
                value = "${tvShow.numberOfSeasons}"
            )

            InfoRow(
                icon = Icons.Default.PlayArrow,
                label = stringResource(Res.string.episodes),
                value = "${tvShow.numberOfEpisodes}"
            )

            if (tvShow.episodeRunTime.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.PlayArrow,
                    label = "Episode Runtime",
                    value = "${tvShow.episodeRunTime.average().toInt()} min"
                )
            }

            if (tvShow.type.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Tv,
                    label = "Type",
                    value = tvShow.type
                )
            }

            InfoRow(
                icon = Icons.Default.Language,
                label = "Original Language",
                value = tvShow.originalLanguage.uppercase()
            )

            if (tvShow.originCountry.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Language,
                    label = "Origin Country",
                    value = tvShow.originCountry.joinToString(", ")
                )
            }
        }
    }
}

@Composable
private fun OverviewSection(tvShow: TvShowDetails) {
    if (tvShow.overview.isNotBlank()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.overview),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = tvShow.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                )

                if (tvShow.tagline.isNotBlank()) {
                    Text(
                        text = "\"${tvShow.tagline}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingsSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ratings & Popularity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RatingCard(
                    title = "Rating",
                    value = "${(tvShow.voteAverage * 10).toInt() / 10.0}",
                    subtitle = "⭐",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RatingCard(
                    title = "Votes",
                    value = "${tvShow.voteCount}",
                    subtitle = "votes",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RatingCard(
                    title = "Popularity",
                    value = "${tvShow.popularity.toInt()}",
                    subtitle = "score",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SeriesInfoSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Series Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            InfoRow(
                icon = Icons.Default.Tv,
                label = "Status",
                value = tvShow.status.ifEmpty { "Unknown" }
            )

            InfoRow(
                icon = Icons.Default.PlayArrow,
                label = "In Production",
                value = if (tvShow.inProduction) "Yes" else "No"
            )

            InfoRow(
                icon = Icons.Default.Tv,
                label = "Total Seasons",
                value = "${tvShow.seasonsCount}"
            )

            if (tvShow.adult) {
                InfoRow(
                    icon = Icons.Default.People,
                    label = "Content Rating",
                    value = "Adult Content"
                )
            }

            if (tvShow.homepage.isNotBlank()) {
                InfoRow(
                    icon = Icons.Default.Language,
                    label = "Official Website",
                    value = tvShow.homepage
                )
            }
        }
    }
}

@Composable
private fun ProductionSection(tvShow: com.example.moviedb.core.model.TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Production",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (tvShow.createdBy.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.People,
                    label = "Created By",
                    value = tvShow.createdBy.joinToString(", ")
                )
            }

            if (tvShow.productionCompanies.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.People,
                    label = "Production Companies",
                    value = tvShow.productionCompanies.joinToString(", ")
                )
            }

            if (tvShow.productionCountries.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Language,
                    label = "Production Countries",
                    value = tvShow.productionCountries.joinToString(", ")
                )
            }
        }
    }
}

@Composable
private fun EpisodesSection(tvShow: com.example.moviedb.core.model.TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Episodes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (tvShow.lastEpisodeName.isNotEmpty()) {
                Text(
                    text = "Last Episode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = tvShow.lastEpisodeName,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (tvShow.lastEpisodeAirDate.isNotEmpty()) {
                    Text(
                        text = "Aired: ${formatDate(tvShow.lastEpisodeAirDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            tvShow.nextEpisodeToAir?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Next Episode",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (tvShow.nextEpisodeAirDate.isNotEmpty()) {
                    Text(
                        text = "Airs: ${formatDate(tvShow.nextEpisodeAirDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GenresSection(tvShow: com.example.moviedb.core.model.TvShowDetails) {
    if (tvShow.genres.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.genres),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tvShow.genres.forEach { genre ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(text = genre) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworksSection(tvShow: com.example.moviedb.core.model.TvShowDetails) {
    if (tvShow.networks.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.networks),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tvShow.networks.forEach { network ->
                        AssistChip(
                            onClick = { },
                            label = { Text(text = network) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagesSection(tvShow: com.example.moviedb.core.model.TvShowDetails) {
    if (tvShow.spokenLanguages.isNotEmpty() || tvShow.languages.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.languages),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (tvShow.spokenLanguages.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = "Spoken Languages",
                        value = tvShow.spokenLanguages.joinToString(", ")
                    )
                }

                if (tvShow.languages.isNotEmpty()) {
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = "Available Languages",
                        value = tvShow.languages.joinToString(", ")
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RatingCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}