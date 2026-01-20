package com.elna.moviedb.core.common

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provides coroutine dispatchers for different execution contexts.
 */
interface AppDispatchers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

expect fun provideAppDispatchers(): AppDispatchers
