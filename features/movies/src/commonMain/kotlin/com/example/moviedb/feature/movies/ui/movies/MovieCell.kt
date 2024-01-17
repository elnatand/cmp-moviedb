package com.example.moviedb.feature.movies.ui.movies

import androidx.compose.foundation.clickable
import com.example.moviedb.core.model.Movie
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MovieCell(
    movie: Movie,
    onClick: (movieId: Int, title: String) -> Unit
) {
    KamelImage(
        modifier = Modifier.clickable {
            onClick(movie.id, movie.title)
        },
        resource = asyncPainterResource("$TMDB_IMAGE_URL${movie.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}