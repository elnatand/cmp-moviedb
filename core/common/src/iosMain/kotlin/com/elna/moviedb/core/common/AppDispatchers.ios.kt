package com.elna.moviedb.core.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

object IOSDispatcherProvider : AppDispatchers {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
}

actual fun provideAppDispatchers(): AppDispatchers = IOSDispatcherProvider
