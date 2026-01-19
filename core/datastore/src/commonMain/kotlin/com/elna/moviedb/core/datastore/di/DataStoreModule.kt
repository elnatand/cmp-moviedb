package com.elna.moviedb.core.datastore.di

import com.elna.moviedb.core.datastore.AppSettingsPreferences
import com.elna.moviedb.core.datastore.AppSettingsPreferencesImpl
import com.elna.moviedb.core.datastore.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.LanguageProvider
import com.elna.moviedb.core.datastore.PaginationPreferences
import com.elna.moviedb.core.datastore.PaginationPreferencesImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for DataStore dependencies.
 * Platform-specific modules should be created for Android and iOS.
 */
expect val platformDataStoreModule: Module

/**
 * Common DataStore module.
 *
 * Provides segregated preference interfaces:
 * - AppSettingsPreferences: For app-level settings (language, theme)
 * - PaginationPreferences: For pagination state (generic category support)
 * - PreferencesManager: Legacy interface for backward compatibility (will be deprecated)
 */
val dataStoreModule = module {
    includes(platformDataStoreModule)

    // Segregated interfaces
    single { AppSettingsPreferencesImpl(get()) } bind AppSettingsPreferences::class
    single { PaginationPreferencesImpl(get()) } bind PaginationPreferences::class

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
