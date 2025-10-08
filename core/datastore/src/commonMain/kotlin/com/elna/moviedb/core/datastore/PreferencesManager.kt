package com.elna.moviedb.core.datastore

import com.elna.moviedb.core.datastore.model.PaginationState
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun getAppLanguageCode(): Flow<String>
    suspend fun setAppLanguageCode(language: AppLanguage)
    fun getAppTheme(): Flow<String>
    suspend fun setAppTheme(theme: AppTheme)
    fun getMoviesPaginationState(): Flow<PaginationState>
    suspend fun saveMoviesPaginationState(state: PaginationState)
    suspend fun clearAll()
}