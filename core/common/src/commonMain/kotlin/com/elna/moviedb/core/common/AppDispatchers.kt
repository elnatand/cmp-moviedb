package com.elna.moviedb.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

const val DISPATCHER_DEFAULT = "default"
const val DISPATCHER_IO = "io"

interface AppDispatcher {
    fun getDispatcher(): CoroutineDispatcher
}

class DefaultAppDispatchers : AppDispatcher {
    override fun getDispatcher(): CoroutineDispatcher {
       return Dispatchers.Default
    }
}

class IoAppDispatchers : AppDispatcher {
    override fun getDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}