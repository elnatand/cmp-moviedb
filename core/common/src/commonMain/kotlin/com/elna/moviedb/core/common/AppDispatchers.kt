package com.elna.moviedb.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Provides coroutine dispatchers for different execution contexts.
 *
 * This sealed interface allows for dependency injection while providing
 * a default implementation via companion object for production use.
 * Tests can provide custom implementations for controllable test dispatchers.
 *
 * Usage:
 * ```
 * class MyRepository(private val appDispatchers: AppDispatchers) {
 *     private val scope = CoroutineScope(appDispatchers.Main)
 *
 *     suspend fun doWork() {
 *         withContext(appDispatchers.IO) { /* IO work */ }
 *     }
 * }
 * ```
 */
sealed interface AppDispatchers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher

    companion object : AppDispatchers {
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val default: CoroutineDispatcher = Dispatchers.Default
    }
}