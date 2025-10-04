package com.elna.moviedb.core.datastore

import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun getAppLanguageCode(): Flow<String>
    suspend fun setAppLanguageCode(language: AppLanguage)
    fun getAppTheme(): Flow<String>
    suspend fun setAppTheme(theme: AppTheme)
    suspend fun clearAll()
}