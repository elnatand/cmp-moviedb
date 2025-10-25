package com.elna.moviedb.core.data

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.AppSettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

/**
 * Interface for components that need to respond to language changes.
 *
 * Following the Observer Pattern - repositories implement this interface
 * to be notified when app language changes without tight coupling.
 *
 * Benefits:
 * - New features can register without modifying coordinator
 * - Coordinator depends on abstraction, not concrete repositories
 * - Loose Coupling: No hardcoded repository dependencies
 */
interface LanguageChangeListener {
    /**
     * Called when the app language changes.
     * Implementations typically clear cached data and reload in the new language.
     */
    suspend fun onLanguageChanged()
}

/**
 * Coordinator that manages language change listeners using the Observer Pattern.
 *
 * New features can register as listeners without modifying this class.
 * Depends only on AppSettingsPreferences for language monitoring.
 *
 * Automatically starts observing language changes upon creation and notifies
 * all registered listeners when the language changes.
 *
 * Usage:
 * ```kotlin
 * val coordinator = LanguageChangeCoordinator(appSettingsPreferences, appDispatchers)
 * // Repositories self-register during their initialization
 * ```
 *
 * @param appSettingsPreferences Source of language change events
 * @param appDispatchers Dispatchers for coroutine scope creation
 */
class LanguageChangeCoordinator(
    private val appSettingsPreferences: AppSettingsPreferences,
    appDispatchers: AppDispatchers,
) {
    private val listeners = mutableSetOf<LanguageChangeListener>()
    private val scope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    init {
        // Auto-start observing language changes
        scope.launch {
            appSettingsPreferences.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on app start
                .collect {
                    // Notify all registered listeners
                    listeners.forEach { listener ->
                        listener.onLanguageChanged()
                    }
                }
        }
    }

    /**
     * Registers a listener to be notified of language changes.
     * Listeners are typically repositories that need to clear/reload data.
     */
    fun registerListener(listener: LanguageChangeListener) {
        listeners.add(listener)
    }
}
