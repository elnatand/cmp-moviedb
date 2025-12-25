package com.elna.moviedb.feature.movies.ui.movies

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
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
import com.elna.moviedb.core.model.MovieCategory
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toPosterUrl

@Composable
fun MovieTile(
    category: MovieCategory,
    movie: Movie,
    onClick: (movieId: Int, title: String) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val imageUrl = movie.posterPath.toPosterUrl()

    val cornerShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
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
            val imageModifier = Modifier
                .height(216.dp)
                .clip(cornerShape)
            val finalModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    imageModifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "${SharedElementKeys.MOVIE_POSTER}${category.name}_${movie.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                        .clip(cornerShape)
                }
            } else {
                imageModifier
            }

            ImageLoader(
                imageUrl = imageUrl,
                modifier = finalModifier
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
