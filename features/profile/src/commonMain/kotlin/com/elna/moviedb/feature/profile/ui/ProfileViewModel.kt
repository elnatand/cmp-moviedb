package com.elna.moviedb.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.common.utils.AppVersion
import com.elna.moviedb.core.datastore.AppSettingsPreferences
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.feature.profile.model.ProfileEvent
import com.elna.moviedb.feature.profile.model.ProfileUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun onEvent(intent: ProfileEvent) {
        when (intent) {
            is ProfileEvent.SetLanguage -> viewModelScope.launch {
                appSettingsPreferences.setAppLanguageCode(intent.language)
            }
            is ProfileEvent.SetTheme -> viewModelScope.launch {
                appSettingsPreferences.setAppTheme(intent.theme)
            }
        }
    }
}
