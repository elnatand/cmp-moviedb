package com.elna.moviedb.feature.person.di

import com.elna.moviedb.feature.person.ui.PersonDetailsViewModel
import org.koin.dsl.module

val personModule = module {

    factory { (id: Int) ->
        PersonDetailsViewModel(
            personId = id,
            personRepository = get()
        )
    }
}