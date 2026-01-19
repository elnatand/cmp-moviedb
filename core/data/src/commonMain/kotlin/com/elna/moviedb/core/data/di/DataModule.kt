package com.elna.moviedb.core.data.di

import com.elna.moviedb.core.data.LanguageChangeCoordinator
import com.elna.moviedb.core.data.strategy.CachingStrategy
import com.elna.moviedb.core.data.strategy.OfflineFirstCachingStrategy
import com.elna.moviedb.core.data.util.LanguageProvider
import org.koin.dsl.module

val dataModule = module {

    // Caching strategy - used by repositories to handle cache/network coordination
    single<CachingStrategy> { OfflineFirstCachingStrategy() }

    single { LanguageProvider(get()) }

    // Language change coordinator - uses Observer Pattern for loose coupling
    // Lazily created when first repository is initialized
    // Repositories self-register during their initialization
    single {
        LanguageChangeCoordinator(
            appSettingsPreferences = get(),
            appDispatchers = get(),
        )
    }
}
