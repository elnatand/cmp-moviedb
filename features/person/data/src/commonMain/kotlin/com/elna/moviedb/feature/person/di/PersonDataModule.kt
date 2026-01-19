package com.elna.moviedb.feature.person.di

import com.elna.moviedb.feature.person.repositories.PersonRepository
import com.elna.moviedb.feature.person.repositories.PersonRepositoryImpl
import org.koin.dsl.module

val personDataModule = module {

    single<PersonRepository> {
        PersonRepositoryImpl(
            personRemoteDataSource = get(),
            languageProvider = get()
        )
    }
}