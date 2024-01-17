package com.example.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import com.example.moviedb.core.model.TvShow
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TvShowCell(
    tvShow: TvShow,
    onClick: (id: Int, title: String) -> Unit
) {
    KamelImage(
        modifier = Modifier.clickable {
            onClick(tvShow.id, tvShow.name)
        },
        resource = asyncPainterResource("$TMDB_IMAGE_URL${tvShow.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}