package com.elna.moviedb.feature.search.data.repositories

import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.search.data.datasources.SearchRemoteDataSource
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchPage
import com.elna.moviedb.feature.search.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val languageProvider: LanguageProvider
) : SearchRepository {

    override suspend fun search(
        filter: SearchFilter,
        query: String,
        page: Int
    ): AppResult<SearchPage> {
        if (query.isBlank()) {
            return AppResult.Success(SearchPage(items = emptyList(), page = 0, totalPages = 0))
        }

        val language = languageProvider.getCurrentLanguage()

        return filter.executeSearch(searchRemoteDataSource, query, page, language)
    }
}