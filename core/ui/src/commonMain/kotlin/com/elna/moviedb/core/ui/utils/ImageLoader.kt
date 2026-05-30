package com.elna.moviedb.core.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.elna.moviedb.resources.Res
import com.elna.moviedb.resources.image
import org.jetbrains.compose.resources.stringResource

/**
 * Shared image component.
 *
 * Uses Coil's [AsyncImage] rather than `SubcomposeAsyncImage`: subcomposition is expensive
 * per item in lazy lists, so loading/error states are rendered as cheap solid-color
 * [ColorPainter] placeholders instead of nested composables. Memory/disk caching and
 * crossfade come from the singleton image loader configured at startup
 * (see core.ui `configureImageLoader`).
 */
@Composable
fun ImageLoader(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    val placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
    val errorPainter = ColorPainter(MaterialTheme.colorScheme.errorContainer)

    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription ?: stringResource(Res.string.image),
        contentScale = contentScale,
        modifier = modifier,
        placeholder = placeholder,
        error = errorPainter,
        fallback = placeholder,
    )
}
