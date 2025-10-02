package com.elna.moviedb.core.datastore.di

import com.elna.moviedb.core.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific DataStore module.
 * Provides DataStore instance using Android Context.
 */
actual val platformDataStoreModule: Module = module {
    single {
        createDataStore(context = get())
    }
}
