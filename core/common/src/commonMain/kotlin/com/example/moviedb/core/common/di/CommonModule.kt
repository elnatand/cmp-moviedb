package com.example.moviedb.core.common.di

import com.example.moviedb.core.common.AppDispatcher
import com.example.moviedb.core.common.DISPATCHER_DEFAULT
import com.example.moviedb.core.common.DISPATCHER_IO
import com.example.moviedb.core.common.DefaultAppDispatchers
import com.example.moviedb.core.common.IoAppDispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single<AppDispatcher>(named(DISPATCHER_DEFAULT)) { DefaultAppDispatchers() }
    single<AppDispatcher>(named(DISPATCHER_IO)) { IoAppDispatchers() }
}
