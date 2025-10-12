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
    fun getPopularMoviesPaginationState(): Flow<PaginationState>
    suspend fun savePopularMoviesPaginationState(state: PaginationState)
    fun getTopRatedMoviesPaginationState(): Flow<PaginationState>
    suspend fun saveTopRatedMoviesPaginationState(state: PaginationState)
    fun getNowPlayingMoviesPaginationState(): Flow<PaginationState>
    suspend fun saveNowPlayingMoviesPaginationState(state: PaginationState)
    suspend fun clearAll()
}