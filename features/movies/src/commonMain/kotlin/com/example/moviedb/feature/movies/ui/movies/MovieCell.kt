package com.example.moviedb.feature.movies.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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

    Column {
        ImageLoader(
            imageUrl = imageUrl,
            modifier = Modifier.clickable { onClick(movie.id, movie.title) }.height(300.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            textAlign = TextAlign.Left,
            text = movie.title,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
