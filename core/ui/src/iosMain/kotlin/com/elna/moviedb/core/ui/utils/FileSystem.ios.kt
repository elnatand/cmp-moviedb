package com.elna.moviedb.core.ui.utils

import coil3.PlatformContext
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun provideCacheDirectory(context: PlatformContext): String? {
    val fileManager = NSFileManager.defaultManager
    val urls = fileManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
    return urls.firstOrNull()?.let { (it as platform.Foundation.NSURL).path }
}
