package com.elna.moviedb.feature.person.ui.components

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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.FilmographyCredit
import com.elna.moviedb.core.model.MediaType
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.movie
import com.elna.moviedb.resources.tv
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FilmographyCard(
    credit: FilmographyCredit,
    onClick: () -> Unit
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                credit.posterPath?.let { posterPath ->
                    ImageLoader(
                        imageUrl = posterPath,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxSize()
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
            }

            // Credit Info
            Column(
                modifier = Modifier
                    .height(80.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = credit.displayTitle,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis
                )

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
                    credit.displayYear?.let { year ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    year,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                when (credit.mediaType) {
                                    MediaType.MOVIE -> stringResource(Res.string.movie)
                                    MediaType.TV -> stringResource(Res.string.tv)
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    }
}
