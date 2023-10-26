package features.movies.di

import features.movies.data.MoviesRepository
import features.movies.data.MoviesRepositoryImpl
import features.movies.data.data_sources.MoviesRemoteDataSource
import features.tv_shows.data.TvShowsRepository
import features.tv_shows.data.TvShowRepositoryImpl
import features.tv_shows.data.data_sources.TvShowsRemoteDataSource
import features.movies.ui.movie_details.MovieDetailsViewModel
import features.movies.ui.movies.MoviesViewModel
import org.koin.dsl.module

val moviesModule = module {
    single { MoviesRemoteDataSource(httpClient = get()) }
    single<MoviesRepository> { MoviesRepositoryImpl(moviesRemoteDataSource = get()) }

    factory {
        MoviesViewModel(
            moviesRepository = get(),
        )
    }

    factory { (id: Int) ->
        MovieDetailsViewModel(
            movieId = id,
            tvShowsRepository = get()
        )
    }
}