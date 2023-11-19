package com.example.moviedb.features.tv_shows.di

import com.example.moviedb.features.tv_shows.ui.tv_shows.TvShowsViewModel
import com.example.moviedb.data.tv_shows.TvShowRepositoryImpl
import com.example.moviedb.data.tv_shows.TvShowsRepository
import com.example.moviedb.data.tv_shows.data_sources.TvShowsRemoteDataSource
import com.example.moviedb.features.tv_shows.ui.tv_show_details.TvShowDetailsViewModel

import org.koin.dsl.module

val tvShowsModule = module {
    single { TvShowsRemoteDataSource(httpClient = get()) }
    single<TvShowsRepository> { TvShowRepositoryImpl(tvShowsRemoteDataSource = get()) }

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