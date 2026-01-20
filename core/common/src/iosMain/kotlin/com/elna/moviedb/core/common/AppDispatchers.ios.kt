package com.elna.moviedb.core.common

import kotlinx.coroutines.Dispatchers

class IOSDispatcherProvider : AppDispatchers {
    override val io = Dispatchers.Default  // iOS doesnt have an dedicated io
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
}

actual fun provideAppDispatchers(): AppDispatchers = IOSDispatcherProvider()
