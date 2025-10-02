package com.elna.moviedb.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager class for accessing DataStore preferences in a type-safe manner.
 * Provides convenience methods for common preference operations.
 *
 * @property dataStore The underlying DataStore instance
 */
class PreferencesManager(private val dataStore: DataStore<Preferences>) {

    /**
     * Preference keys used throughout the app
     */
    object PreferenceKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")
        // Add more preference keys as needed
    }

    /**
     * Get a string preference as a Flow
     */
    fun getStringPreference(key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Set a string preference
     */
    suspend fun setStringPreference(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * Get language preference
     */
    fun getLanguage(): Flow<String> {
        return getStringPreference(PreferenceKeys.LANGUAGE, "en")
    }

    /**
     * Set language preference
     */
    suspend fun setLanguage(language: String) {
        setStringPreference(PreferenceKeys.LANGUAGE, language)
    }

    /**
     * Get theme preference
     */
    fun getTheme(): Flow<String> {
        return getStringPreference(PreferenceKeys.THEME, "system")
    }

    /**
     * Set theme preference
     */
    suspend fun setTheme(theme: String) {
        setStringPreference(PreferenceKeys.THEME, theme)
    }

    /**
     * Clear all preferences
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
