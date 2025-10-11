package com.elna.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
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
import com.elna.moviedb.core.model.TvShow
import com.elna.moviedb.core.ui.utils.ImageLoader

@Composable
fun TvShowTile(
    tvShow: TvShow,
    onClick: (id: Int, title: String) -> Unit
) {
    val imageUrl = tvShow.poster_path ?: ""

    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick(tvShow.id, tvShow.name) }
    ) {
        // TV Show Poster
        Card(
            modifier = Modifier
                .width(140.dp)
                .height(210.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                ImageLoader(
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // TV Show Title
        Text(
            text = tvShow.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(140.dp)
                .padding(top = 8.dp)
        )
    }
}
