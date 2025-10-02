package com.elna.moviedb.core.datastore.di

import com.elna.moviedb.core.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific DataStore module.
 * Provides DataStore instance for iOS platform.
 */
actual val platformDataStoreModule: Module = module {
    single {
        createDataStore()
    }
}
