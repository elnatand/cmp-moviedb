package features.tv_shows.di

import features.tv_shows.ui.tv_shows.TvShowsViewModel
import data.tv_shows.TvShowRepositoryImpl
import data.tv_shows.TvShowsRepository
import data.tv_shows.data_sources.TvShowsRemoteDataSource
import features.tv_shows.ui.tv_show_details.TvShowDetailsViewModel

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