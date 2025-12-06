package com.elna.moviedb.core.common.di

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.common.utils.AppVersion
import com.elna.moviedb.core.common.utils.AppVersionImpl
import org.koin.dsl.module

val commonModule = module {
    single<AppDispatchers> { AppDispatchers }
    single<AppVersion> { AppVersionImpl() }
}
