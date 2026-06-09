package com.elna.moviedb.core.ui.utils

import coil3.PlatformContext

actual fun provideCacheDirectory(context: PlatformContext): String? {
    return context.cacheDir.absolutePath
}
