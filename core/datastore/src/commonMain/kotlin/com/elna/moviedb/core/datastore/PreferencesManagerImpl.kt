package com.elna.moviedb.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elna.moviedb.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager class for accessing DataStore preferences in a type-safe manner.
 * Provides convenience methods for common preference operations.
 *
 * @property dataStore The underlying DataStore instance
 */
internal class PreferencesManagerImpl(private val dataStore: DataStore<Preferences>) :
    PreferencesManager {

    /**
     * Preference keys used throughout the app
     */
    private object PreferenceKeys {
        val LANGUAGE = stringPreferencesKey("language")
    }

    /**
     * Get a string preference as a Flow
     */
    private fun getStringPreference(
        key: Preferences.Key<String>,
        defaultValue: String = ""
    ): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Set a string preference
     */
    private suspend fun setStringPreference(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * Get language preference
     */
    override fun getAppLanguageCode(): Flow<String> {
        return getStringPreference(PreferenceKeys.LANGUAGE, AppLanguage.ENGLISH.code)
    }

    /**
     * Set language preference
     */
    override suspend fun setAppLanguageCode(language: AppLanguage) {
        setStringPreference(PreferenceKeys.LANGUAGE, language.code)
    }

    /**
     * Clear all preferences
     */
    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
