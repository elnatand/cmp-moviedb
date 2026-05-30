package com.elna.moviedb.core.network.client

import android.util.Log

internal actual fun platformNetworkLog(message: String) {
    Log.d("HttpClient", message)
}
