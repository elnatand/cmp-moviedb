package com.elna.moviedb.feature.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toImageUrl
import com.elna.moviedb.core.model.SearchResultItem

@Composable
fun SearchResultItem(
    item: SearchResultItem,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onItemClicked() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImageLoader(
                imageUrl = when (item) {
                    is SearchResultItem.MovieItem -> item.movie.posterPath.toImageUrl()
                  //  is SearchResultItem.TvShowItem -> item.tvShow.posterPath.toImageUrl()
                    is SearchResultItem.PersonItem -> item.profilePath.toImageUrl()
                },
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = when (item) {
                        is SearchResultItem.MovieItem -> item.movie.title
                    //    is SearchResultItem.TvShowItem -> item.tvShow.name
                        is SearchResultItem.PersonItem -> item.name
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                when (item) {
                    is SearchResultItem.PersonItem -> {
                        item.knownForDepartment?.let { department ->
                            Text(
                                text = department,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    else -> {
                        item.overview?.let { overview ->
                            Text(
                                text = overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.voteAverage?.let { rating ->
                        if (rating > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${(rating * 10).toInt() / 10.0}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    when (item) {
                        is SearchResultItem.MovieItem -> {
                            item.releaseDate?.let { date ->
                                if (date.isNotBlank()) {
                                    Text(
                                        text = date.take(4), // Show only year
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
//                        is SearchResultItem.TvShowItem -> {
//                            item.firstAirDate?.let { date ->
//                                if (date.isNotBlank()) {
//                                    Text(
//                                        text = date.take(4), // Show only year
//                                        style = MaterialTheme.typography.bodySmall,
//                                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                                    )
//                                }
//                            }
//                        }
                        is SearchResultItem.PersonItem -> {
                            // No additional date info for person
                        }
                    }
                }
            }
        }
    }
}