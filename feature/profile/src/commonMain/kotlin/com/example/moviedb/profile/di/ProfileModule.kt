package com.example.moviedb.profile.di

import com.example.moviedb.profile.ui.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
//    single { TvShowsRemoteDataSource(httpClient = get()) }
//    single { TvShowsRemoteDataSource(httpClient = get()) }
//    single<TvShowsRepository> { TvShowRepositoryImpl(tvShowsRemoteDataSource = get()) }
//
    factory {
        ProfileViewModel()
    }
//
//    factory { (id: Int) ->
//        TvShowDetailsViewModel(
//            tvShowId = id,
//            tvShowsRepository = get()
//        )
//    }
}