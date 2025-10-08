package com.elna.moviedb.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.elna.moviedb.core.datastore.model.PaginationState
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
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
        val THEME = stringPreferencesKey("theme")
        val MOVIES_CURRENT_PAGE = intPreferencesKey("movies_current_page")
        val MOVIES_TOTAL_PAGES = intPreferencesKey("movies_total_pages")
        val MOVIES_LAST_UPDATED = longPreferencesKey("movies_last_updated")
        val MOVIES_LANGUAGE = stringPreferencesKey("movies_language")
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
     * Get theme preference
     */
    override fun getAppTheme(): Flow<String> {
        return getStringPreference(PreferenceKeys.THEME, AppTheme.SYSTEM.value)
    }

    /**
     * Set theme preference
     */
    override suspend fun setAppTheme(theme: AppTheme) {
        setStringPreference(PreferenceKeys.THEME, theme.value)
    }

    /**
     * Get movies pagination state
     */
    override fun getMoviesPaginationState(): Flow<PaginationState> {
        return dataStore.data.map { preferences ->
            PaginationState(
                currentPage = preferences[PreferenceKeys.MOVIES_CURRENT_PAGE] ?: 0,
                totalPages = preferences[PreferenceKeys.MOVIES_TOTAL_PAGES] ?: 0,
                lastUpdated = preferences[PreferenceKeys.MOVIES_LAST_UPDATED] ?: 0L,
                language = preferences[PreferenceKeys.MOVIES_LANGUAGE] ?: ""
            )
        }
    }

    /**
     * Save movies pagination state
     */
    override suspend fun saveMoviesPaginationState(state: PaginationState) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.MOVIES_CURRENT_PAGE] = state.currentPage
            preferences[PreferenceKeys.MOVIES_TOTAL_PAGES] = state.totalPages
            preferences[PreferenceKeys.MOVIES_LAST_UPDATED] = state.lastUpdated
            preferences[PreferenceKeys.MOVIES_LANGUAGE] = state.language
        }
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
