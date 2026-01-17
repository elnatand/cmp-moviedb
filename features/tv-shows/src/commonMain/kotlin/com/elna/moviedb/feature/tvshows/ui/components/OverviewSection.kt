package com.elna.moviedb.feature.tvshows.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.tvshows.model.TvShowDetails
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.overview
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OverviewSection(tvShow: TvShowDetails) {
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
