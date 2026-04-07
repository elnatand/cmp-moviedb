package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.model.TvShowCategory
import com.elna.moviedb.core.ui.navigation.SharedElementKeys
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toPosterUrl

@Composable
fun TvShowTile(
    category: TvShowCategory,
    tvShow: TvShow,
    onClick: (id: Int, title: String) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
) {
    val imageUrl = tvShow.posterPath.toPosterUrl()
    val cornerShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .width(128.dp)
            .height(192.dp),
        shape = cornerShape,
        onClick = { onClick(tvShow.id, tvShow.name) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageModifier = Modifier
                .width(128.dp)
                .height(192.dp)
                .clip(cornerShape)

            val finalModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                with(sharedTransitionScope) {
                    imageModifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "${SharedElementKeys.TV_SHOW_POSTER}${category.name}-${tvShow.id}"
                            ),
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.5f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.9f)
                        )
                    )
            )

            Text(
                text = tvShow.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}
