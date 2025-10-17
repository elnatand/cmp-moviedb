package com.elna.moviedb.core.data

import com.elna.moviedb.core.datastore.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

/**
 * Generic coordinator that reacts to language changes and executes a provided action.
 *
 * This class separates the concern of "reacting to language changes" from the repository's
 * responsibility of "providing data access". When the app language changes, this coordinator
 * executes the provided lambda to handle the language change (e.g., clearing cache and reloading data).
 *
 * **Design Rationale:**
 * - **Single Responsibility:** Repository provides data (reactive), coordinator monitors changes (proactive)
 * - **Testability:** Can test language observation logic independently from data access
 * - **Explicit Lifecycle:** Scope is injected, making lifecycle management clear
 * - **Reusability:** Generic design allows usage with any repository or action
 * - **Interface Contract:** Language observation is visible at the DI level, not hidden in repository
 *
 * **Example Usage:**
 * ```kotlin
 * // For Movies
 * single {
 *     LanguageChangeCoordinator(
 *         preferencesManager = get(),
 *         scope = CoroutineScope(SupervisorJob() + get<AppDispatchers>().main),
 *         onLanguageChange = { get<MoviesRepository>().clearAndReload() }
 *     )
 * }
 *
 * // For TV Shows
 * single {
 *     LanguageChangeCoordinator(
 *         preferencesManager = get(),
 *         scope = CoroutineScope(SupervisorJob() + get<AppDispatchers>().main),
 *         onLanguageChange = { get<TvShowsRepository>().clearAndReload() }
 *     )
 * }
 * ```
 *
 * @param preferencesManager Source of language change events
 * @param scope Coroutine scope for language observation (typically application-scoped)
 * @param onLanguageChange Action to execute when language changes (typically clearing cache and reloading data)
 */
class LanguageChangeCoordinator(
    preferencesManager: PreferencesManager,
    scope: CoroutineScope,
    onLanguageChange: suspend () -> Unit
) {
    init {
        // Observe language changes and trigger the provided action
        scope.launch {
            preferencesManager.getAppLanguageCode()
                .distinctUntilChanged()
                .drop(1) // Skip initial emission to avoid clearing on app start
                .collect {
                    // When language changes, execute the provided action
                    onLanguageChange()
                }
        }
    }
}
