package com.elna.moviedb.core.datastore.di

import com.elna.moviedb.core.datastore.settings.AppSettingsPreferences
import com.elna.moviedb.core.datastore.settings.AppSettingsPreferencesImpl
import com.elna.moviedb.core.datastore.language.LanguageChangeCoordinator
import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.datastore.pagination.PaginationPreferences
import com.elna.moviedb.core.datastore.pagination.PaginationPreferencesImpl
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
