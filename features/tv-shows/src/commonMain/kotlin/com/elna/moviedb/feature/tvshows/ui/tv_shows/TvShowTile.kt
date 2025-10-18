package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.ui.utils.ImageLoader
import com.elna.moviedb.core.ui.utils.toPosterUrl
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TvShowTile(
    tvShow: TvShow,
    onClick: (id: Int, title: String) -> Unit
) {
    val imageUrl = tvShow.posterPath.toPosterUrl()

    Card(
        modifier = Modifier.width(144.dp),
        onClick = { onClick(tvShow.id, tvShow.name) },
        shape = RoundedCornerShape(8.dp),
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
                text = tvShow.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun TvShowTilePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            TvShowTile(
                tvShow = TvShow(
                    id = 1,
                    name = "Law and Order: Special Victims Unit",
                    posterPath = "/ggFHVNu6YYI5L9pCfOacjizRGt.jpg"
                ),
                onClick = { _, _ -> }
            )
        }
    }
}
