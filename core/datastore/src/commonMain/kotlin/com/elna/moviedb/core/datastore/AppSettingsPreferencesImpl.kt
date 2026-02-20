package com.elna.moviedb.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of AppSettingsPreferences using DataStore.
 *
 * Manages app-level settings like language and theme preferences.
 *
 * @property dataStore The underlying DataStore instance
 */
internal class AppSettingsPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : AppSettingsPreferences {

    private object PreferenceKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")
    }

    override fun getAppLanguageCode(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.LANGUAGE] ?: AppLanguage.ENGLISH.code
        }
    }

    override suspend fun setAppLanguageCode(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language.code
        }
    }

    override fun getThemeConfig(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.THEME] ?: ThemeConfig.SYSTEM.value
        }
    }

    override suspend fun setThemeConfig(theme: ThemeConfig) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME] = theme.value
        }
    }
}
