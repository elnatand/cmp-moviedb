package com.elna.moviedb.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val POPULAR_MOVIES_CURRENT_PAGE = intPreferencesKey("popular_movies_current_page")
        val POPULAR_MOVIES_TOTAL_PAGES = intPreferencesKey("popular_movies_total_pages")
        val TOP_RATED_MOVIES_CURRENT_PAGE = intPreferencesKey("top_rated_movies_current_page")
        val TOP_RATED_MOVIES_TOTAL_PAGES = intPreferencesKey("top_rated_movies_total_pages")
        val NOW_PLAYING_MOVIES_CURRENT_PAGE = intPreferencesKey("now_playing_movies_current_page")
        val NOW_PLAYING_MOVIES_TOTAL_PAGES = intPreferencesKey("now_playing_movies_total_pages")
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
     * Get popular movies pagination state
     */
    override fun getPopularMoviesPaginationState(): Flow<PaginationState> {
        return dataStore.data.map { preferences ->
            PaginationState(
                currentPage = preferences[PreferenceKeys.POPULAR_MOVIES_CURRENT_PAGE] ?: 0,
                totalPages = preferences[PreferenceKeys.POPULAR_MOVIES_TOTAL_PAGES] ?: 0
            )
        }
    }

    /**
     * Save popular movies pagination state
     */
    override suspend fun savePopularMoviesPaginationState(state: PaginationState) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.POPULAR_MOVIES_CURRENT_PAGE] = state.currentPage
            preferences[PreferenceKeys.POPULAR_MOVIES_TOTAL_PAGES] = state.totalPages
        }
    }

    /**
     * Get top rated movies pagination state
     */
    override fun getTopRatedMoviesPaginationState(): Flow<PaginationState> {
        return dataStore.data.map { preferences ->
            PaginationState(
                currentPage = preferences[PreferenceKeys.TOP_RATED_MOVIES_CURRENT_PAGE] ?: 0,
                totalPages = preferences[PreferenceKeys.TOP_RATED_MOVIES_TOTAL_PAGES] ?: 0
            )
        }
    }

    /**
     * Save top rated movies pagination state
     */
    override suspend fun saveTopRatedMoviesPaginationState(state: PaginationState) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TOP_RATED_MOVIES_CURRENT_PAGE] = state.currentPage
            preferences[PreferenceKeys.TOP_RATED_MOVIES_TOTAL_PAGES] = state.totalPages
        }
    }

    /**
     * Get now playing movies pagination state
     */
    override fun getNowPlayingMoviesPaginationState(): Flow<PaginationState> {
        return dataStore.data.map { preferences ->
            PaginationState(
                currentPage = preferences[PreferenceKeys.NOW_PLAYING_MOVIES_CURRENT_PAGE] ?: 0,
                totalPages = preferences[PreferenceKeys.NOW_PLAYING_MOVIES_TOTAL_PAGES] ?: 0
            )
        }
    }

    /**
     * Save now playing movies pagination state
     */
    override suspend fun saveNowPlayingMoviesPaginationState(state: PaginationState) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOW_PLAYING_MOVIES_CURRENT_PAGE] = state.currentPage
            preferences[PreferenceKeys.NOW_PLAYING_MOVIES_TOTAL_PAGES] = state.totalPages
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
