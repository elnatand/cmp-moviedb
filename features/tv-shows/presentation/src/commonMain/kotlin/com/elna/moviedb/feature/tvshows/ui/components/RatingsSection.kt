package com.elna.moviedb.feature.tvshows.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.elna.moviedb.resources.popularity
import com.elna.moviedb.resources.rating
import com.elna.moviedb.resources.ratings_popularity
import com.elna.moviedb.resources.score
import com.elna.moviedb.resources.votes
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RatingsSection(tvShow: TvShowDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.ratings_popularity),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RatingCard(
                    title = stringResource(Res.string.rating),
                    value = "${((tvShow.voteAverage ?: 0.0) * 10).toInt() / 10.0}",
                    subtitle = "⭐",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RatingCard(
                    title = stringResource(Res.string.votes),
                    value = "${tvShow.voteCount ?: 0}",
                    subtitle = stringResource(Res.string.votes),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                RatingCard(
                    title = stringResource(Res.string.popularity),
                    value = "${(tvShow.popularity ?: 0.0).toInt()}",
                    subtitle = stringResource(Res.string.score),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
