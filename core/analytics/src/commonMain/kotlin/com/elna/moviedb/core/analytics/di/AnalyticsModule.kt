package com.elna.moviedb.core.analytics.di

import com.elna.moviedb.core.analytics.AnalyticsService
import com.elna.moviedb.core.analytics.createAnalyticsService
import org.koin.core.module.Module
import org.koin.dsl.module

val analyticsModule: Module = module {
    single<AnalyticsService> { createAnalyticsService() }
}
