package com.elna.moviedb.feature.tvshows.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.core.ui.utils.formatDate
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.aired_prefix
import com.elna.moviedb.resources.airs_prefix
import com.elna.moviedb.resources.episodes
import com.elna.moviedb.resources.last_episode
import com.elna.moviedb.resources.next_episode
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EpisodesSection(tvShow: TvShowDetails) {
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
                        text = "${stringResource(Res.string.aired_prefix)}${
                            formatDate(
                                lastEpisodeAirDate
                            )
                        }",
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
                        text = "${stringResource(Res.string.airs_prefix)}${
                            formatDate(
                                nextEpisodeAirDate
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
