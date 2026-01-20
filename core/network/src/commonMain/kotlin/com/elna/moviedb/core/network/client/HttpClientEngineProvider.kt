package com.elna.moviedb.core.network.client

import io.ktor.client.engine.HttpClientEngineFactory

expect fun provideHttpClientEngine(): HttpClientEngineFactory<*>
