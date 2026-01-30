package com.elna.moviedb.feature.search.di

import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.feature.search.navigation.SearchNavigationFactory
import com.elna.moviedb.feature.search.ui.SearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val searchModule = module {
    viewModelOf(::SearchViewModel)
    factoryOf(::SearchNavigationFactory) bind NavigationFactory::class
}
