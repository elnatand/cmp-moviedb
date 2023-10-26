package features.movies.di

import features.tv_shows.data.TvShowsRepository
import features.tv_shows.data.TvShowRepositoryImpl
import features.tv_shows.data.data_sources.TvShowsRemoteDataSource
import features.movies.ui.movie_details.MovieDetailsViewModel
import features.movies.ui.movies.MoviesViewModel
import org.koin.dsl.module

val moviesModule = module {
    single { TvShowsRemoteDataSource(httpClient = get()) }
    single<TvShowsRepository> { TvShowRepositoryImpl(tvShowsRemoteDataSource = get()) }

    factory {
        MoviesViewModel(
            tvShowsRepository = get(),
        )
    }

    factory { (id: Int) ->
        MovieDetailsViewModel(
            movieId = id,
            tvShowsRepository = get()
        )
    }
}