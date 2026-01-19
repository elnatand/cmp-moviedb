package com.elna.moviedb.feature.search.data.repositories

import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.SearchRemoteDataSource
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import com.elna.moviedb.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val languageProvider: LanguageProvider
) : SearchRepository {

    override fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): Flow<AppResult<List<SearchResultItem>>> = flow {
        if (query.isBlank()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val language = languageProvider.getCurrentLanguage()

        val result = filter.executeSearch(searchRemoteDataSource, query, page, language)

        emit(result)
    }
}