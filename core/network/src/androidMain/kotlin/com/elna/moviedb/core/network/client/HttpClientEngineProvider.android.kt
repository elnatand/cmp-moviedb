package com.elna.moviedb.core.network.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> {
    return Android
}
