package com.elna.moviedb.feature.tvshows.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.TvShowDetails
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.available_languages
import com.elna.moviedb.resources.languages
import com.elna.moviedb.resources.spoken_languages
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LanguagesSection(tvShow: TvShowDetails) {
    val hasSpokenLanguages = tvShow.spokenLanguages?.isNotEmpty() == true
    val hasLanguages = tvShow.languages?.isNotEmpty() == true
    if (hasSpokenLanguages || hasLanguages) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.languages),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                tvShow.spokenLanguages?.takeIf { it.isNotEmpty() }?.let { spokenLanguages ->
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = stringResource(Res.string.spoken_languages),
                        value = spokenLanguages.joinToString(", ")
                    )
                }

                tvShow.languages?.takeIf { it.isNotEmpty() }?.let { languages ->
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = stringResource(Res.string.available_languages),
                        value = languages.joinToString(", ")
                    )
                }
            }
        }
    }
}
