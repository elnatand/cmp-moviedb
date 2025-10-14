package com.elna.moviedb.feature.tvshows.ui.tv_show_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.core.ui.utils.formatDate
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.episode_runtime
import com.elna.moviedb.resources.episodes
import com.elna.moviedb.resources.first_air_date
import com.elna.moviedb.resources.last_air_date
import com.elna.moviedb.resources.minutes_suffix
import com.elna.moviedb.resources.origin_country
import com.elna.moviedb.resources.original_language
import com.elna.moviedb.resources.seasons
import com.elna.moviedb.resources.series_information
import com.elna.moviedb.resources.type
import com.elna.moviedb.resources.unknown
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BasicInfoSection(tvShow: TvShowDetails) {
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
                value = formatDate(
                    tvShow.firstAirDate ?: ""
                ).ifEmpty { stringResource(Res.string.unknown) }
            )

            tvShow.lastAirDate?.takeIf { it.isNotEmpty() }?.let { lastAirDate ->
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(Res.string.last_air_date),
                    value = formatDate(lastAirDate)
                )
            }

            InfoRow(
                icon = Icons.AutoMirrored.Filled.List,
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
                    value = "${episodeRunTime.average().toInt()} ${stringResource(Res.string.minutes_suffix)}"
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
