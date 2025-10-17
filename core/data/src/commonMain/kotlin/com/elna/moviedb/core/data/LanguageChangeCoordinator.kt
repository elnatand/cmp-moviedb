package com.elna.moviedb.core.data

import com.elna.moviedb.core.datastore.AppSettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

/**
 * Generic coordinator that reacts to language changes and executes a provided action.
 *
 * Following Interface Segregation Principle - depends only on AppSettingsPreferences
 * for language monitoring, not the full PreferencesManager with pagination methods.
 *
 * @param appSettingsPreferences Source of language change events
 * @param scope Coroutine scope for language observation (typically application-scoped)
 * @param onLanguageChange Action to execute when language changes (typically clearing cache and reloading data)
 */
class LanguageChangeCoordinator(
    appSettingsPreferences: AppSettingsPreferences,
    scope: CoroutineScope,
    onLanguageChange: suspend () -> Unit
) {
    init {
        // Observe language changes and trigger the provided action
        scope.launch {
            appSettingsPreferences.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on app start
                .collect {
                    // When language changes, execute the provided action
                    onLanguageChange()
                }
        }
    }
}
