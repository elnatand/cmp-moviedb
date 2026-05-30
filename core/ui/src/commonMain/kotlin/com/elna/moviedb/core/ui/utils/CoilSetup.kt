package com.elna.moviedb.core.ui.utils

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.request.crossfade

/**
 * Installs the singleton Coil [ImageLoader] used by every [ImageLoader] composable.
 *
 * Configures a memory cache (so posters aren't re-decoded while scrolling a lazy list) and a
 * crossfade. Call once at app startup, before any image is requested.
 * [SingletonImageLoader.setSafe] is idempotent — it won't overwrite an already-set loader — so
 * calling this more than once is harmless.
 */
fun configureImageLoader() {
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .build()
    }
}
