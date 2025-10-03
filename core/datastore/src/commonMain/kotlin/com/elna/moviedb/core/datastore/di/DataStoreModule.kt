package com.elna.moviedb.core.datastore.di

import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.datastore.PreferencesManagerImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for DataStore dependencies.
 * Platform-specific modules should be created for Android and iOS.
 */
expect val platformDataStoreModule: Module

/**
 * Common DataStore module that provides PreferencesManager.
 */
val dataStoreModule = module {
    includes(platformDataStoreModule)

    single { PreferencesManagerImpl(get()) } bind PreferencesManager::class
}
