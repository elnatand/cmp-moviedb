package com.elna.moviedb.feature.tvshows.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.adult_content
import com.elna.moviedb.resources.content_rating
import com.elna.moviedb.resources.in_production
import com.elna.moviedb.resources.no
import com.elna.moviedb.resources.official_website
import com.elna.moviedb.resources.series_status
import com.elna.moviedb.resources.status
import com.elna.moviedb.resources.total_seasons
import com.elna.moviedb.resources.unknown
import com.elna.moviedb.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SeriesInfoSection(tvShow: TvShowDetails) {
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

            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.InfoRow(
                icon = Icons.Default.Tv,
                label = stringResource(Res.string.status),
                value = tvShow.status?.ifEmpty { stringResource(Res.string.unknown) }
                    ?: stringResource(Res.string.unknown)
            )

            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.InfoRow(
                icon = Icons.Default.PlayArrow,
                label = stringResource(Res.string.in_production),
                value = if (tvShow.inProduction == true) stringResource(Res.string.yes) else stringResource(
                    Res.string.no
                )
            )

            _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.InfoRow(
                icon = Icons.AutoMirrored.Filled.List,
                label = stringResource(Res.string.total_seasons),
                value = "${tvShow.seasonsCount ?: 0}"
            )

            if (tvShow.adult == true) {
                _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.InfoRow(
                    icon = Icons.Default.People,
                    label = stringResource(Res.string.content_rating),
                    value = stringResource(Res.string.adult_content)
                )
            }

            tvShow.homepage?.takeIf { it.isNotBlank() }?.let { homepage ->
                val uriHandler = LocalUriHandler.current
                _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.InfoRow(
                    icon = Icons.Default.Language,
                    label = stringResource(Res.string.official_website),
                    value = homepage,
                    onClick = { uriHandler.openUri(homepage) }
                )
            }
        }
    }
}
