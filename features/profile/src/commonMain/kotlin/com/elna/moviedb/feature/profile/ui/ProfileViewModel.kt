package com.elna.moviedb.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val selectedLanguage: StateFlow<String> = preferencesManager.getAppLanguageCode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH.code
        )

    val selectedTheme: StateFlow<String> = preferencesManager.getAppTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM.value
        )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.setAppLanguageCode(language)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.setAppTheme(theme)
        }
    }
}
