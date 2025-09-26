package com.example.moviedb.feature.movies.ui.movie_details

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import com.example.moviedb.core.model.MovieDetails
import com.example.moviedb.core.ui.design_system.MovieDbLoader
import com.example.moviedb.core.ui.utils.ImageLoader
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieDetailsScreen(
    movieId: Int,
) {
    val viewModel = koinViewModel<MovieDetailsViewModel> { parametersOf(movieId) }
    val uiState by viewModel.uiState.collectAsState()

    MovieDetailsScreen(
        uiState = uiState,
        onRetry = viewModel::retry
    )
}


@Composable
private fun MovieDetailsScreen(
    uiState: MovieDetailsViewModel.UiState,
    onRetry: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        when (uiState) {
            is MovieDetailsViewModel.UiState.Loading -> {
                MovieDbLoader()
            }

            is MovieDetailsViewModel.UiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    onRetry = onRetry
                )
            }

            is MovieDetailsViewModel.UiState.Success -> {
                MovieDetailsContent(movie = uiState.movieDetails)
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Error loading movie details",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}


@Composable
private fun MovieDetailsContent(movie: MovieDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section with Backdrop and Poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
        ) {
            // Backdrop Image
            movie.backdrop_path?.let { backdropPath ->
                ImageLoader(
                    imageUrl = "$TMDB_IMAGE_URL$backdropPath",
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


            movie.poster_path?.let { posterPath ->
                Card(
                    modifier = Modifier
                        .systemBarsPadding()
                        .width(120.dp)
                        .height(180.dp)
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    ImageLoader(
                        imageUrl = "$TMDB_IMAGE_URL$posterPath",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Title and Basic Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                movie.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                // Rating
                movie.vote_average?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "${(rating * 10).toInt() / 10.0}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        movie.vote_count?.let { count ->
                            Text(
                                text = "($count votes)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

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
                movie.release_date?.let { date ->
                    InfoItem(
                        icon = Icons.Default.DateRange,
                        label = "Release",
                        value = date.take(4)
                    )
                }

                movie.runtime?.let { runtime ->
                    InfoItem(
                        icon = Icons.Default.Timer,
                        label = "Runtime",
                        value = "${runtime}min"
                    )
                }

                movie.original_language?.let { language ->
                    InfoItem(
                        icon = Icons.Default.Language,
                        label = "Language",
                        value = language.uppercase()
                    )
                }
            }

            // Overview Section
            movie.overview.takeIf { it.isNotBlank() }?.let { overview ->
                SectionCard(
                    title = "Overview",
                    content = {
                        Text(
                            text = overview,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                        )
                    }
                )
            }

            // Genres Section
            movie.genres?.takeIf { it.isNotEmpty() }?.let { genres ->
                SectionCard(
                    title = "Genres",
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
                    title = "Box Office",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            movie.budget?.takeIf { it > 0 }?.let { budget ->
                                BoxOfficeItem(
                                    label = "Budget",
                                    amount = budget
                                )
                            }
                            movie.revenue?.takeIf { it > 0 }?.let { revenue ->
                                BoxOfficeItem(
                                    label = "Revenue",
                                    amount = revenue
                                )
                            }
                        }
                    }
                )
            }

            // Production Section
            movie.production_companies?.takeIf { it.isNotEmpty() }?.let { companies ->
                SectionCard(
                    title = "Production Companies",
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
            movie.production_countries?.takeIf { it.isNotEmpty() }?.let { countries ->
                SectionCard(
                    title = "Production Countries",
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
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
        }
    }
}

@Composable
private fun BoxOfficeItem(
    label: String,
    amount: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$$amount",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
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
        poster_path = "/sample_poster.jpg",
        backdrop_path = "/sample_backdrop.jpg",
        release_date = "2023-12-01",
        vote_average = 8.5,
        vote_count = 1250,
        runtime = 142,
        tagline = "An amazing cinematic experience",
        genres = listOf("Action", "Adventure", "Sci-Fi"),
        original_language = "en",
        budget = 150000000,
        revenue = 750000000,
        production_companies = listOf("Marvel Studios", "Disney"),
        production_countries = listOf("United States", "United Kingdom"),
        adult = false,
        homepage = "https://example.com",
        imdb_id = "tt1234567",
        original_title = "Sample Movie",
        popularity = 85.5,
        status = "Released",
        spoken_languages = listOf("English", "Spanish")
    )

    MovieDetailsScreen(
        uiState = MovieDetailsViewModel.UiState.Success(sampleMovie),
        onRetry = {}
    )
}

@Preview
@Composable
private fun MovieDetailsScreenLoadingPreview() {
    MovieDetailsScreen(
        uiState = MovieDetailsViewModel.UiState.Loading,
        onRetry = {}
    )
}

@Preview
@Composable
private fun MovieDetailsScreenErrorPreview() {
    MovieDetailsScreen(
        uiState = MovieDetailsViewModel.UiState.Error("Failed to load movie details. Please check your internet connection."),
        onRetry = {}
    )
}
