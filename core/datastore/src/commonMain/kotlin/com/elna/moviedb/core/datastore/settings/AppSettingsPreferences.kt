package com.elna.moviedb.core.datastore.settings

import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing app-level settings preferences.
 * Contains only app settings methods.
 *
 * Clients that only need app settings (like ProfileViewModel) depend on this
 * interface instead of the full PreferencesManager, reducing unnecessary dependencies.
 */
interface AppSettingsPreferences {
    /**
     * Observes the current app language code.
     *
     * @return Flow emitting the language code (e.g., "en", "fr")
     */
    fun getAppLanguageCode(): Flow<String>

    /**
     * Sets the app language.
     *
     * @param language The language to set
     */
    suspend fun setAppLanguageCode(language: AppLanguage)

    /**
     * Observes the current app theme.
     *
     * @return Flow emitting the theme value
     */
    fun getAppTheme(): Flow<String>

    /**
     * Sets the app theme.
     *
     * @param theme The theme to set
     */
    suspend fun setAppTheme(theme: AppTheme)
}
