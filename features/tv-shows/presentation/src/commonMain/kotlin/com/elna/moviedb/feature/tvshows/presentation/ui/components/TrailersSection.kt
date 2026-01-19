package com.elna.moviedb.feature.tvshows.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.trailers
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TrailersSection(tvShow: TvShowDetails) {
    tvShow.trailers?.takeIf { it.isNotEmpty() }?.let { trailers ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.trailers),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trailers) { trailer ->
                        _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.ui.components.TrailerCard(
                            trailer = trailer
                        )
                    }
                }
            }
        }
    }
}
