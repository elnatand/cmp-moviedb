package com.elna.moviedb.feature.movies.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.MovieDetails
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toBackdropUrl
import com.elna.moviedb.core.ui.utils.toPosterUrl
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.rating
import com.elna.moviedb.resources.votes
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MovieHeroSection(
    movie: MovieDetails,
    category: String? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        // Backdrop Image
        ImageLoader(
            imageUrl = movie.backdropPath.toBackdropUrl(),
            modifier = Modifier.fillMaxSize()
        )

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

        // Poster
        Card(
            modifier = Modifier
                .systemBarsPadding()
                .width(120.dp)
                .height(180.dp)
                .align(Alignment.TopStart)
                .padding(start = 16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            val imageModifier = Modifier.fillMaxSize()
            val sharedElementKey = if (category != null) {
                "${SharedElementKeys.MOVIE_POSTER}${category}_${movie.id}"
            } else {
                "${SharedElementKeys.MOVIE_POSTER}${movie.id}"
            }
            val finalModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    imageModifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = sharedElementKey),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                }
            } else {
                imageModifier
            }

            ImageLoader(
                imageUrl = movie.posterPath.toPosterUrl(),
                modifier = finalModifier
            )
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
                    fontStyle = FontStyle.Italic
                )
            }

            // Rating
            movie.voteAverage?.let { rating ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(Res.string.rating),
                        tint = Color.Yellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${(rating * 10).toInt() / 10.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    movie.voteCount?.let { count ->
                        Text(
                            text = "($count ${stringResource(Res.string.votes)})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
