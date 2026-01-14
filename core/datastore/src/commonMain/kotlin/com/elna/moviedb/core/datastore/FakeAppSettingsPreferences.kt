package com.elna.moviedb.core.datastore

import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * Fake implementation of AppSettingsPreferences for testing.
 * Uses MutableStateFlow to simulate reactive preference storage.
 */
class FakeAppSettingsPreferences : AppSettingsPreferences {
    private val languageFlow = MutableStateFlow(AppLanguage.ENGLISH.code)
    private val themeFlow = MutableStateFlow(AppTheme.SYSTEM.value)

    var lastSetLanguage: AppLanguage? = null
        private set

    var lastSetTheme: AppTheme? = null
        private set

    override fun getAppLanguageCode(): Flow<String> = languageFlow

    override suspend fun setAppLanguageCode(language: AppLanguage) {
        lastSetLanguage = language
        languageFlow.value = language.code
    }

    override fun getAppTheme(): Flow<String> = themeFlow

    override suspend fun setAppTheme(theme: AppTheme) {
        lastSetTheme = theme
        themeFlow.value = theme.value
    }
}
