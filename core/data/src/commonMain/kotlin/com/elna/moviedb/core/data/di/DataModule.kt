package com.elna.moviedb.core.data.di

import com.elna.moviedb.core.datastore.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.LanguageProvider
import org.koin.dsl.module

val dataModule = module {



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
