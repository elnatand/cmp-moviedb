package com.elna.moviedb.feature.search.data.di

import com.elna.moviedb.feature.search.data.SearchRepositoryImpl
import com.elna.moviedb.feature.search.domain.data_sources.SearchRepository
import org.koin.dsl.module

val searchDataModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            searchRemoteDataSource = get(),
            languageProvider = get()
        )
    }
}

