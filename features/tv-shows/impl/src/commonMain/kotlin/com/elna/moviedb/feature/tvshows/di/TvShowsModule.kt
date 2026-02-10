package com.elna.moviedb.feature.tvshows.di

import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.feature.tvshows.navigation.TvShowsNavigationFactory
import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsViewModel
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val tvShowsModule = module {

    factoryOf(::TvShowsViewModel)

    factory { (id: Int) ->
        TvShowDetailsViewModel(
            tvShowId = id,
            tvShowsRepository = get()
        )
    }

    factoryOf(::TvShowsNavigationFactory) bind NavigationFactory::class

}
