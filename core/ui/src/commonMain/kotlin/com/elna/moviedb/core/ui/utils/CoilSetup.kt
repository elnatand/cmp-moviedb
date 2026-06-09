package com.elna.moviedb.core.ui.utils

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Installs the singleton Coil [ImageLoader] used by every [ImageLoader] composable.
 *
 * Configures a memory cache (so posters aren't re-decoded while scrolling a lazy list), a
 * disk cache (for persistence across launches), and a crossfade. Call once at app startup.
 * [SingletonImageLoader.setSafe] is idempotent — it won't overwrite an already-set loader.
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
            .diskCache {
                provideCacheDirectory(context)?.let { cachePath ->
                    DiskCache.Builder()
                        .directory(cachePath.toPath().resolve("image_cache"))
                        .fileSystem(FileSystem.SYSTEM)
                        .maxSizePercent(0.02) // Use 2% of disk space for images
                        .build()
                }
            }
            .build()
    }
}
