package com.example.moviedb.tvshows.ui.tv_shows

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.example.moviedb.model.TvShow
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
        resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${tvShow.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}