package com.elna.moviedb.feature.tvshows.di

import com.elna.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsViewModel
import com.elna.moviedb.feature.tvshows.ui.tv_shows.TvShowsViewModel
import org.koin.dsl.module

val tvShowsModule = module {


    factory {
        TvShowsViewModel(
            tvShowsRepository = get(),
        )
    }

    factory { (id: Int) ->
        TvShowDetailsViewModel(
            tvShowId= id,
            tvShowsRepository = get()
        )
    }
}