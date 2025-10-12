package com.elna.moviedb.feature.movies.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.Movie
import com.elna.moviedb.core.ui.utils.ImageLoader

@Composable
fun MovieTile(
    movie: Movie,
    onClick: (movieId: Int, title: String) -> Unit
) {
    val imageUrl = movie.poster_path ?: ""

    Card(
        modifier = Modifier.width(144.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { onClick(movie.id, movie.title) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.width(140.dp)
        ) {
            // Movie Poster with overlay
            Box(
                modifier = Modifier
                    .width(144.dp)
                    .height(216.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                ImageLoader(
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .width(144.dp)
                    .height(64.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
