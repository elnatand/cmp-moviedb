package com.elna.moviedb.feature.tvshows.ui.tv_show_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.core.ui.design_system.AppErrorComponent
import com.elna.moviedb.core.ui.design_system.AppLoader
import com.elna.moviedb.core.ui.utils.ImageLoader
import org.jetbrains.compose.resources.stringResource
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.overview
import com.elna.moviedb.resources.genres
import com.elna.moviedb.resources.unknown
import com.elna.moviedb.resources.series_information
import com.elna.moviedb.resources.first_air_date
import com.elna.moviedb.resources.last_air_date
import com.elna.moviedb.resources.seasons
import com.elna.moviedb.resources.episodes
import com.elna.moviedb.resources.networks
import com.elna.moviedb.resources.languages
import com.elna.moviedb.resources.episode_runtime
import com.elna.moviedb.resources.type
import com.elna.moviedb.resources.original_language
import com.elna.moviedb.resources.origin_country
import com.elna.moviedb.resources.ratings_popularity
import com.elna.moviedb.resources.rating
import com.elna.moviedb.resources.votes
import com.elna.moviedb.resources.popularity
import com.elna.moviedb.resources.score
import com.elna.moviedb.resources.series_status
import com.elna.moviedb.resources.status
import com.elna.moviedb.resources.in_production
import com.elna.moviedb.resources.total_seasons
import com.elna.moviedb.resources.content_rating
import com.elna.moviedb.resources.adult_content
import com.elna.moviedb.resources.official_website
import com.elna.moviedb.resources.production
import com.elna.moviedb.resources.created_by
import com.elna.moviedb.resources.production_companies
import com.elna.moviedb.resources.production_countries
import com.elna.moviedb.resources.last_episode
import com.elna.moviedb.resources.next_episode
import com.elna.moviedb.resources.aired_prefix
import com.elna.moviedb.resources.airs_prefix
import com.elna.moviedb.resources.spoken_languages
import com.elna.moviedb.resources.available_languages
import com.elna.moviedb.resources.yes
import com.elna.moviedb.resources.no
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
        tvShow.backdropPath?.takeIf { it.isNotEmpty() }?.let { backdropPath ->
            ImageLoader(
                imageUrl = backdropPath,
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
                    imageUrl = "${tvShow.posterPath ?: ""}",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Title and Basic Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tvShow.name ?: "Unknown",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                val originalName = tvShow.originalName
                if (originalName != null && originalName != tvShow.name) {
                    Text(
                        text = originalName,
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
                            text = "${((tvShow.voteAverage ?: 0.0) * 10).toInt() / 10.0}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    // Status
                    tvShow.status?.takeIf { it.isNotEmpty() }?.let { status ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = status,
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
                value = formatDate(tvShow.firstAirDate ?: "").ifEmpty { stringResource(Res.string.unknown) }
            )

            tvShow.lastAirDate?.takeIf { it.isNotEmpty() }?.let { lastAirDate ->
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(Res.string.last_air_date),
                    value = formatDate(lastAirDate)
                )
            }

            InfoRow(
                icon = Icons.Default.Tv,
                label = stringResource(Res.string.seasons),
                value = "${tvShow.numberOfSeasons ?: 0}"
            )

            InfoRow(
                icon = Icons.Default.PlayArrow,
                label = stringResource(Res.string.episodes),
                value = "${tvShow.numberOfEpisodes ?: 0}"
            )

            tvShow.episodeRunTime?.takeIf { it.isNotEmpty() }?.let { episodeRunTime ->
                InfoRow(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(Res.string.episode_runtime),
                    value = "${episodeRunTime.average().toInt()} min"
                )
            }

            tvShow.type?.takeIf { it.isNotEmpty() }?.let { type ->
                InfoRow(
                    icon = Icons.Default.Tv,
                    label = stringResource(Res.string.type),
                    value = type
                )
            }

            InfoRow(
                icon = Icons.Default.Language,
                label = stringResource(Res.string.original_language),
                value = (tvShow.originalLanguage ?: "").uppercase()
            )

            tvShow.originCountry?.takeIf { it.isNotEmpty() }?.let { originCountry ->
                InfoRow(
                    icon = Icons.Default.Language,
                    label = stringResource(Res.string.origin_country),
                    value = originCountry.joinToString(", ")
                )
            }
        }
    }
}

@Composable
private fun OverviewSection(tvShow: TvShowDetails) {
    val overview = tvShow.overview
    if (!overview.isNullOrBlank()) {
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
                    text = overview,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                )

                tvShow.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                    Text(
                        text = "\"$tagline\"",
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
                text = stringResource(Res.string.ratings_popularity),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RatingCard(
                    title = stringResource(Res.string.rating),
                    value = "${((tvShow.voteAverage ?: 0.0) * 10).toInt() / 10.0}",
                    subtitle = "⭐",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RatingCard(
                    title = stringResource(Res.string.votes),
                    value = "${tvShow.voteCount ?: 0}",
                    subtitle = stringResource(Res.string.votes),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RatingCard(
                    title = stringResource(Res.string.popularity),
                    value = "${(tvShow.popularity ?: 0.0).toInt()}",
                    subtitle = stringResource(Res.string.score),
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
                text = stringResource(Res.string.series_status),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            InfoRow(
                icon = Icons.Default.Tv,
                label = stringResource(Res.string.status),
                value = tvShow.status?.ifEmpty { stringResource(Res.string.unknown) } ?: stringResource(Res.string.unknown)
            )

            InfoRow(
                icon = Icons.Default.PlayArrow,
                label = stringResource(Res.string.in_production),
                value = if (tvShow.inProduction == true) stringResource(Res.string.yes) else stringResource(Res.string.no)
            )

            InfoRow(
                icon = Icons.Default.Tv,
                label = stringResource(Res.string.total_seasons),
                value = "${tvShow.seasonsCount ?: 0}"
            )

            if (tvShow.adult == true) {
                InfoRow(
                    icon = Icons.Default.People,
                    label = stringResource(Res.string.content_rating),
                    value = stringResource(Res.string.adult_content)
                )
            }

            tvShow.homepage?.takeIf { it.isNotBlank() }?.let { homepage ->
                val uriHandler = LocalUriHandler.current
                InfoRow(
                    icon = Icons.Default.Language,
                    label = stringResource(Res.string.official_website),
                    value = homepage,
                    onClick = { uriHandler.openUri(homepage) }
                )
            }
        }
    }
}

@Composable
private fun ProductionSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.production),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            tvShow.createdBy?.takeIf { it.isNotEmpty() }?.let { createdBy ->
                InfoRow(
                    icon = Icons.Default.People,
                    label = stringResource(Res.string.created_by),
                    value = createdBy.joinToString(", ")
                )
            }

            tvShow.productionCompanies?.takeIf { it.isNotEmpty() }?.let { productionCompanies ->
                InfoRow(
                    icon = Icons.Default.People,
                    label = stringResource(Res.string.production_companies),
                    value = productionCompanies.joinToString(", ")
                )
            }

            tvShow.productionCountries?.takeIf { it.isNotEmpty() }?.let { productionCountries ->
                InfoRow(
                    icon = Icons.Default.Language,
                    label = stringResource(Res.string.production_countries),
                    value = productionCountries.joinToString(", ")
                )
            }
        }
    }
}

@Composable
private fun EpisodesSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.episodes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            tvShow.lastEpisodeName?.takeIf { it.isNotEmpty() }?.let { lastEpisodeName ->
                Text(
                    text = stringResource(Res.string.last_episode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = lastEpisodeName,
                    style = MaterialTheme.typography.bodyMedium
                )
                tvShow.lastEpisodeAirDate?.takeIf { it.isNotEmpty() }?.let { lastEpisodeAirDate ->
                    Text(
                        text = "${stringResource(Res.string.aired_prefix)}${formatDate(lastEpisodeAirDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            tvShow.nextEpisodeToAir?.takeIf { it.isNotEmpty() }?.let { nextEpisodeToAir ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.next_episode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = nextEpisodeToAir,
                    style = MaterialTheme.typography.bodyMedium
                )
                tvShow.nextEpisodeAirDate?.takeIf { it.isNotEmpty() }?.let { nextEpisodeAirDate ->
                    Text(
                        text = "${stringResource(Res.string.airs_prefix)}${formatDate(nextEpisodeAirDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GenresSection(tvShow: TvShowDetails) {
    tvShow.genres?.takeIf { it.isNotEmpty() }?.let { genres ->
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
                    genres.forEach { genre ->
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
private fun NetworksSection(tvShow: TvShowDetails) {
    tvShow.networks?.takeIf { it.isNotEmpty() }?.let { networks ->
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
                    networks.forEach { network ->
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
private fun LanguagesSection(tvShow: TvShowDetails) {
    val hasSpokenLanguages = tvShow.spokenLanguages?.isNotEmpty() == true
    val hasLanguages = tvShow.languages?.isNotEmpty() == true
    if (hasSpokenLanguages || hasLanguages) {
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

                tvShow.spokenLanguages?.takeIf { it.isNotEmpty() }?.let { spokenLanguages ->
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = stringResource(Res.string.spoken_languages),
                        value = spokenLanguages.joinToString(", ")
                    )
                }

                tvShow.languages?.takeIf { it.isNotEmpty() }?.let { languages ->
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = stringResource(Res.string.available_languages),
                        value = languages.joinToString(", ")
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
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it },
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
            modifier = Modifier.weight(1f),
            color = if (onClick != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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