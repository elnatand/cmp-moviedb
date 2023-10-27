package features.tv_shows.di

import features.tv_shows.ui.tv_shows.TvShowsViewModel
import features.tv_shows.data.TvShowRepositoryImpl
import features.tv_shows.data.TvShowsRepository
import features.tv_shows.data.data_sources.TvShowsRemoteDataSource
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
            tvShowId = id,
            tvShowsRepository = get()
        )
    }
}