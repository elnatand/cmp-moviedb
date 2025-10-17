package com.elna.moviedb.core.datastore.di

import com.elna.moviedb.core.datastore.AppSettingsPreferences
import com.elna.moviedb.core.datastore.AppSettingsPreferencesImpl
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
 * Common DataStore module following Interface Segregation Principle.
 *
 * Provides segregated preference interfaces:
 * - AppSettingsPreferences: For app-level settings (language, theme)
 * - PaginationPreferences: For pagination state (generic category support)
 * - PreferencesManager: Legacy interface for backward compatibility (will be deprecated)
 */
val dataStoreModule = module {
    includes(platformDataStoreModule)

    // Segregated interfaces (ISP compliant)
    single { AppSettingsPreferencesImpl(get()) } bind AppSettingsPreferences::class
    single { PaginationPreferencesImpl(get()) } bind PaginationPreferences::class
}
