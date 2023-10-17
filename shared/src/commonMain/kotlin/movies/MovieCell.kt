package movies

import movies.model.Movie
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MovieCell(movie: Movie) {
    KamelImage(
        resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${movie.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}