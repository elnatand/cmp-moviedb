package com.example.moviedb.feature.tvshows.di

import com.example.moviedb.feature.tvshows.ui.tv_show_details.TvShowDetailsViewModel
import com.example.moviedb.feature.tvshows.ui.tv_shows.TvShowsViewModel
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