package features.tv_shows.di

import features.tv_shows.ui.tv_shows.TvShowsViewModel
import features.tv_shows.data.TvShowRepositoryImpl
import features.tv_shows.data.TvShowsRepository
import features.tv_shows.data.data_sources.TvShowsRemoteDataSource
import org.koin.dsl.module

val tvShowsModule = module {
    single { TvShowsRemoteDataSource(httpClient = get()) }
    single<TvShowsRepository> { TvShowRepositoryImpl(tvShowsRemoteDataSource = get()) }

    factory {
        TvShowsViewModel(
            tvShowsRepository = get(),
        )
    }
}