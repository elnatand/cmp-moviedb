package features.movies.di


import data.movies.MoviesRepository
import data.movies.MoviesRepositoryImpl
import data.movies.data_sources.MoviesRemoteDataSource
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
            moviesRepository = get()
        )
    }
}