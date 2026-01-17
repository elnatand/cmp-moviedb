package com.elna.moviedb.feature.tvshows.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.feature.tvshows.model.TvShowDetails
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.created_by
import com.elna.moviedb.resources.production
import com.elna.moviedb.resources.production_companies
import com.elna.moviedb.resources.production_countries
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductionSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.production),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            tvShow.createdBy?.takeIf { it.isNotEmpty() }?.let { createdBy ->
                InfoRow(
                    icon = Icons.Default.People,
                    label = stringResource(Res.string.created_by),
                    value = createdBy.joinToString(", ")
                )
            }

            tvShow.productionCompanies?.takeIf { it.isNotEmpty() }?.let { productionCompanies ->
                InfoRow(
                    icon = Icons.Default.People,
                    label = stringResource(Res.string.production_companies),
                    value = productionCompanies.joinToString(", ")
                )
            }

            tvShow.productionCountries?.takeIf { it.isNotEmpty() }?.let { productionCountries ->
                InfoRow(
                    icon = Icons.Default.Language,
                    label = stringResource(Res.string.production_countries),
                    value = productionCountries.joinToString(", ")
                )
            }
        }
    }
}
