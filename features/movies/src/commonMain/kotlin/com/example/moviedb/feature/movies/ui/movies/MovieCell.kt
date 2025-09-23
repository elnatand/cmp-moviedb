package com.example.moviedb.feature.movies.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb.core.data.model.TMDB_IMAGE_URL
import com.example.moviedb.core.model.Movie
import com.example.moviedb.core.ui.utils.ImageLoader

@Composable
fun MovieCell(
    movie: Movie,
    onClick: (movieId: Int, title: String) -> Unit
) {
    val imageUrl = "$TMDB_IMAGE_URL${movie.poster_path ?: ""}"

    ImageLoader(
        imageUrl = imageUrl,
        modifier = Modifier.clickable { onClick(movie.id, movie.title) }.height(300.dp)
    )
}