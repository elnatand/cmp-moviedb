package com.elna.moviedb.core.datastore

import com.elna.moviedb.core.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun getAppLanguageCode(): Flow<String>
    suspend fun setAppLanguageCode(language: AppLanguage)
    suspend fun clearAll()
}