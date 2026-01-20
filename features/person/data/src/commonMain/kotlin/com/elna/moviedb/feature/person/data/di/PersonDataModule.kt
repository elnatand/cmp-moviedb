package com.elna.moviedb.feature.person.data.di

import com.elna.moviedb.feature.person.data.datasources.PersonRemoteDataSource
import com.elna.moviedb.feature.person.data.repositories.PersonRepositoryImpl
import com.elna.moviedb.feature.person.domain.repositories.PersonRepository
import org.koin.dsl.module

val personDataModule = module {

    // Remote data source - handles person API calls
    single {
        PersonRemoteDataSource(
            apiClient = get()
        )
    }

    single<PersonRepository> {
        PersonRepositoryImpl(
            personRemoteDataSource = get(),
            languageProvider = get()
        )
    }
}