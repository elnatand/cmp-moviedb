package com.elna.moviedb.feature.search.data.di

import com.elna.moviedb.feature.search.data.repositories.SearchRepositoryImpl
import com.elna.moviedb.feature.search.domain.repository.SearchRepository
import org.koin.dsl.module

val searchDataModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            searchRemoteDataSource = get(),
            languageProvider = get()
        )
    }
}

