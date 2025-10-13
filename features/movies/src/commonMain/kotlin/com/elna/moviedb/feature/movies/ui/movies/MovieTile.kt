package com.elna.moviedb.feature.movies.ui.movies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    val imageUrl = movie.posterPath ?: ""

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
            modifier = Modifier.fillMaxWidth()
        ) {
            ImageLoader(
                imageUrl = imageUrl,
                modifier = Modifier.height(216.dp)
            )

            Text(
                modifier = Modifier.height(64.dp).padding(8.dp),
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
