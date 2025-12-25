package com.elna.moviedb.feature.person.ui.components

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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.FilmographyCredit
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toPosterUrl
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun FilmographyCard(
    credit: FilmographyCredit,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .padding(2.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Poster Image
            val cornerShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            credit.posterPath?.let { posterPath ->
                val sharedElementKey = when (credit.mediaType) {
                    MediaType.MOVIE -> "${SharedElementKeys.MOVIE_POSTER}${credit.id}"
                    MediaType.TV -> "${SharedElementKeys.TV_SHOW_POSTER}${credit.id}"
                }

                val posterModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = sharedElementKey),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .clip(cornerShape)
                    }
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(cornerShape)
                }

                ImageLoader(
                    imageUrl = posterPath.toPosterUrl(),
                    modifier = posterModifier
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Credit Info
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = credit.displayTitle,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                credit.character?.let { character ->
                    Text(
                        text = character,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (credit.mediaType) {
                            MediaType.MOVIE -> Icons.Default.Movie
                            MediaType.TV -> Icons.Default.Tv
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )

                    credit.displayYear?.let { year ->
                        Text(
                            year,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilmographyCardPreview_Movie() {
    FilmographyCard(
        credit = FilmographyCredit(
            id = 1,
            title = "The Dark Knight",
            name = null,
            character = "Bruce Wayne / Batman",
            posterPath = null,
            releaseDate = "2008-07-18",
            firstAirDate = null,
            mediaType = MediaType.MOVIE,
            voteAverage = 9.0
        ),
        onClick = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun FilmographyCardPreview_TV() {
    FilmographyCard(
        credit = FilmographyCredit(
            id = 2,
            title = null,
            name = "Breaking Bad",
            character = "Walter White",
            posterPath = null,
            releaseDate = null,
            firstAirDate = "2008-01-20",
            mediaType = MediaType.TV,
            voteAverage = 9.5
        ),
        onClick = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun FilmographyCardPreview_NoCharacter() {
    FilmographyCard(
        credit = FilmographyCredit(
            id = 3,
            title = "Inception",
            name = null,
            character = null,
            posterPath = null,
            releaseDate = "2010-07-16",
            firstAirDate = null,
            mediaType = MediaType.MOVIE,
            voteAverage = 8.8
        ),
        onClick = { }
    )
}
