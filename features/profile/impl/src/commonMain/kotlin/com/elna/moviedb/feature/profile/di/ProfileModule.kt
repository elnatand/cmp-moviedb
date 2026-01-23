package com.elna.moviedb.feature.profile.di

import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.feature.profile.navigation.ProfileNavigationFactory
import com.elna.moviedb.feature.profile.ui.ProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val profileModule = module {
    factoryOf(::ProfileViewModel)
    factoryOf(::ProfileNavigationFactory) bind NavigationFactory::class
}
