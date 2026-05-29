package com.elna.moviedb.feature.tvshows.presentation.di

import com.elna.moviedb.feature.tvshows.presentation.ui.tv_show_details.TvShowDetailsViewModel
import com.elna.moviedb.feature.tvshows.presentation.ui.tv_shows.TvShowsViewModel
import org.koin.dsl.module

val tvShowsPresentationModule = module {
    factory {
        TvShowsViewModel(
            tvShowsRepository = get(),
        )
    }

    factory { (id: Int) ->
        TvShowDetailsViewModel(
            tvShowId = id,
            tvShowsRepository = get()
        )
    }
}