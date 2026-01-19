package com.elna.moviedb.feature.person.data.di

import com.elna.moviedb.feature.person.domain.repositories.PersonRepository
import com.elna.moviedb.feature.person.data.repositories.PersonRepositoryImpl
import org.koin.dsl.module

val personDataModule = module {

    single<PersonRepository> {
        PersonRepositoryImpl(
            personRemoteDataSource = get(),
            languageProvider = get()
        )
    }
}