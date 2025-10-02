package com.elna.moviedb.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elna.moviedb.core.datastore.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    fun getLanguage(): String {
        return ""
    }

    init {

        viewModelScope.launch {
            val x = preferencesManager.getLanguage()
            println("datastoreLanguage: ${x.first()}")
        }
    }
}