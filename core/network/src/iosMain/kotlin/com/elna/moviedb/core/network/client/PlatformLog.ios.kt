package com.elna.moviedb.core.network.client

import platform.Foundation.NSLog

internal actual fun platformNetworkLog(message: String) {
    NSLog("HttpClient: %@", message)
}
