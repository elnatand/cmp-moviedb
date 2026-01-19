package com.elna.moviedb.feature.person.presentation.di

import com.elna.moviedb.feature.person.presentation.ui.PersonDetailsViewModel
import org.koin.dsl.module

val personPresentationModule = module {

    factory { (id: Int) ->
        PersonDetailsViewModel(
            personId = id,
            personRepository = get()
        )
    }
}