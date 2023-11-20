package com.example.moviedb.features.movies.ui.movies

import androidx.compose.foundation.clickable
import com.example.moviedb.model.Movie
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
        resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${movie.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}