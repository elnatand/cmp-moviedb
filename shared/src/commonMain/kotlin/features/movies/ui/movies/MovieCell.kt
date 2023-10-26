package features.movies.ui.movies

import androidx.compose.foundation.clickable
import features.tv_shows.model.TvShow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MovieCell(
    tvShow: TvShow,
    onClick: (Int) -> Unit
) {
    KamelImage(
        modifier = Modifier.clickable {
            onClick(tvShow.id)
        },
        resource = asyncPainterResource("https://image.tmdb.org/t/p/w300${tvShow.poster_path ?: ""}"),
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}