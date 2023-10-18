package movies.ui.movies

import androidx.compose.foundation.clickable
import movies.model.Movie
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MovieCell(
    movie: Movie,
    onClick: (Int) -> Unit
) {
    KamelImage(
        modifier = Modifier.clickable {
            onClick(movie.id)
        },
        resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${movie.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}