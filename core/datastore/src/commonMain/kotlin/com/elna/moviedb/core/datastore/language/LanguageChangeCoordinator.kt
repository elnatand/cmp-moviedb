package com.elna.moviedb.core.datastore.language

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.datastore.settings.AppSettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

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
    // All access to `listeners` is confined to `scope`'s dispatcher, but confinement alone
    // does NOT prevent interleaving: onLanguageChanged() suspends, and a suspension point
    // yields the thread, letting a queued registerListener() mutate the set mid-dispatch.
    // The notification loop below therefore iterates a snapshot (listeners.toList()) to avoid
    // a ConcurrentModificationException when a repository self-registers (e.g. during lazy DI
    // construction) while a language change is being dispatched to existing listeners.
    private val listeners = mutableSetOf<LanguageChangeListener>()
    private val scope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    init {
        // Auto-start observing language changes
        scope.launch {
            appSettingsPreferences.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on app start
                .collect {
                    // Notify all registered listeners. Iterate a snapshot so a listener that
                    // self-registers mid-dispatch (across an onLanguageChanged() suspension)
                    // can't structurally modify the set we're iterating. Each notification is
                    // isolated: a failure in one listener must not kill the collection
                    // coroutine, otherwise language changes would silently stop working for the
                    // rest of the session. Cancellation is rethrown to honor structured
                    // concurrency.
                    listeners.toList().forEach { listener ->
                        try {
                            listener.onLanguageChanged()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Exception) {
                            // Listener-local failure (e.g. a reload that threw) — skip it
                            // and continue notifying the rest.
                        }
                    }
                }
        }
    }

    /**
     * Registers a listener to be notified of language changes.
     * Listeners are typically repositories that need to clear/reload data.
     *
     * Registration is marshalled onto the coordinator's main-confined scope so it
     * never races with in-flight notification dispatch.
     */
    fun registerListener(listener: LanguageChangeListener) {
        scope.launch {
            listeners.add(listener)
        }
    }
}
