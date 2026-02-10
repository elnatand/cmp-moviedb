package com.elna.moviedb.feature.person.di

import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.feature.person.navigation.PersonDetailsNavigationFactory
import com.elna.moviedb.feature.person.ui.PersonDetailsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val personModule = module {

    factory { (id: Int) ->
        PersonDetailsViewModel(
            personId = id,
            personRepository = get()
        )
    }

    factoryOf(::PersonDetailsNavigationFactory) bind NavigationFactory::class

}
