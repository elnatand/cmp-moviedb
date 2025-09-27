package com.elna.moviedb.core.common.di

import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.common.DISPATCHER_DEFAULT
import com.elna.moviedb.core.common.DISPATCHER_IO
import com.elna.moviedb.core.common.DefaultAppDispatchers
import com.elna.moviedb.core.common.IoAppDispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single<AppDispatcher>(named(DISPATCHER_DEFAULT)) { DefaultAppDispatchers() }
    single<AppDispatcher>(named(DISPATCHER_IO)) { IoAppDispatchers() }
}
