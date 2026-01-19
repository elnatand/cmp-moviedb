package com.elna.moviedb.feature.profile.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.common.utils.AppVersion
import com.elna.moviedb.core.datastore.settings.AppSettingsPreferences
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.feature.profile.presentation.model.ProfileEvent
import com.elna.moviedb.feature.profile.presentation.model.ProfileUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel following MVI (Model-View-Intent) pattern for Profile screen.
 *
 * MVI Components:
 * - Model: [ProfileUiState] - Immutable state representing the UI
 * - View: ProfileScreen - Renders the state and dispatches intents
 * - Intent: [ProfileEvent] - User actions/intentions
 *
 * Depends only on AppSettingsPreferences,
 * not the full PreferencesManager with pagination methods it doesn't need.
 */
class ProfileViewModel(
    private val appSettingsPreferences: AppSettingsPreferences,
    private val appVersion: AppVersion
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        appSettingsPreferences.getAppLanguageCode(),
        appSettingsPreferences.getAppTheme()
    ) { languageCode, theme ->
        ProfileUiState(
            selectedLanguageCode = languageCode,
            selectedThemeValue = theme,
            appVersion = appVersion.getAppVersion()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(
            selectedLanguageCode = AppLanguage.ENGLISH.code,
            selectedThemeValue = AppTheme.SYSTEM.value,
            appVersion = appVersion.getAppVersion()
        )
    )

    /**
     * Main entry point for handling user intents.
     * All UI interactions should go through this method.
     */
    fun onEvent(intent: ProfileEvent) {
        when (intent) {
            is ProfileEvent.SetLanguage -> setLanguage(intent.language)
            is ProfileEvent.SetTheme -> setTheme(intent.theme)
        }
    }

    private fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            appSettingsPreferences.setAppLanguageCode(language)
        }
    }

    private fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            appSettingsPreferences.setAppTheme(theme)
        }
    }
}
