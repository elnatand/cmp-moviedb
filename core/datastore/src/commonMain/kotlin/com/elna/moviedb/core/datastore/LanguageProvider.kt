package com.elna.moviedb.core.datastore

import com.elna.moviedb.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Provides language-related utilities for the application.
 *
 * This class centralizes language formatting logic to eliminate code duplication
 * across repositories. Previously, the same language formatting logic was duplicated
 * in 4+ repositories (MoviesRepository, TvShowsRepository, SearchRepository, PersonRepository).
 *
 * Following DRY (Don't Repeat Yourself) principle.
 *
 * @param appSettingsPreferences Source of language preferences
 */
class LanguageProvider(
    private val appSettingsPreferences: AppSettingsPreferences
) {

    /**
     * Gets the current language in the format required by TMDB API (e.g., "en-US", "fr-FR").
     *
     * This method combines the language code with the country code.
     *
     * @return Formatted language string (e.g., "en-US")
     */
    suspend fun getCurrentLanguage(): String {
        val languageCode = appSettingsPreferences.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }

    /**
     * Observes the current language in the format required by TMDB API.
     *
     * Returns a Flow that emits the formatted language string whenever the
     * app language changes.
     *
     * @return Flow emitting formatted language strings (e.g., "en-US")
     */
    fun observeCurrentLanguage(): Flow<String> {
        return appSettingsPreferences.getAppLanguageCode().map { languageCode ->
            val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
            "$languageCode-$countryCode"
        }
    }
}
