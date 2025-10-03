package com.elna.moviedb.core.common.di

import com.elna.moviedb.core.common.AppDispatchers
import org.koin.dsl.module

val commonModule = module {
    single<AppDispatchers> { AppDispatchers }
}
