package com.elna.moviedb.core.ui.utils

import coil3.PlatformContext

/**
 * Returns a platform-specific directory suitable for disk caching.
 * Returns null if the platform doesn't support or provide a cache directory.
 */
expect fun provideCacheDirectory(context: PlatformContext): String?
