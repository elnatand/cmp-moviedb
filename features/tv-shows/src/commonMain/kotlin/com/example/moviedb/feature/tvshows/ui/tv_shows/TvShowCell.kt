package com.example.moviedb.feature.tvshows.ui.tv_shows

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import com.example.moviedb.core.model.TvShow
import com.example.moviedb.core.ui.utils.ImageLoader


@Composable
fun TvShowCell(
    tvShow: TvShow,
    onClick: (id: Int, title: String) -> Unit
) {
    ImageLoader(
        modifier = Modifier.clickable {
            onClick(tvShow.id, tvShow.name)
        },
        imageUrl = "$TMDB_IMAGE_URL${tvShow.poster_path ?: ""}",
    )
}