package com.elna.moviedb.feature.tvshows.presentation.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toBackdropUrl
import com.elna.moviedb.core.ui.utils.toPosterUrl
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.rating
import com.elna.moviedb.resources.unknown
import com.elna.moviedb.resources.votes
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HeroSection(
    tvShow: TvShowDetails,
    category: String? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        tvShow.backdropPath?.takeIf { it.isNotEmpty() }?.let { backdropPath ->
            ImageLoader(
                imageUrl = backdropPath.toBackdropUrl(),
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

        // Content Row with Poster and Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Poster Card - Bottom Left
            tvShow.posterPath?.takeIf { it.isNotEmpty() }?.let { posterPath ->
                val sharedElementKey = if (category != null) {
                    "${SharedElementKeys.TV_SHOW_POSTER}${category}-${tvShow.id}"
                } else {
                    "${SharedElementKeys.TV_SHOW_POSTER}${tvShow.id}"
                }

                val cornerShape = RoundedCornerShape(12.dp)
                val cardModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier
                            .width(110.dp)
                            .height(165.dp)
                            .clip(cornerShape)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = sharedElementKey),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    }
                } else {
                    Modifier
                        .width(110.dp)
                        .height(165.dp)
                }

                Card(
                    modifier = cardModifier,
                    shape = cornerShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    ImageLoader(
                        imageUrl = posterPath.toPosterUrl(),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(cornerShape)
                    )
                }
            }

            // Info Text - Right of Poster
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = tvShow.name ?: stringResource(Res.string.unknown),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                tvShow.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontStyle = FontStyle.Italic,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }

                // Rating
                tvShow.voteAverage?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(Res.string.rating),
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${(rating * 10).toInt() / 10.0}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        tvShow.voteCount?.let { count ->
                            Text(
                                text = "($count ${stringResource(Res.string.votes)})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
