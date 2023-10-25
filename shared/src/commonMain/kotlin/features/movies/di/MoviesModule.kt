package features.movies.di

import features.movies.data.MoviesRepository
import features.movies.data.MoviesRepositoryImpl
import features.movies.data.data_sources.MoviesRemoteDataSource
import features.movies.ui.movie_details.MovieDetailsViewModel
import features.movies.ui.movies.MoviesViewModel
import network.createHttpClient
import org.koin.dsl.module

val moviesModule = module {
    //single { createHttpClient(httpClientEngine = get()) }
    single { MoviesRemoteDataSource(httpClient = get()) }
    single<MoviesRepository> { MoviesRepositoryImpl(moviesRemoteDataSource = get()) }

    factory { (id: Int, ) ->
        MovieDetailsViewModel(
            movieId = id,
            moviesRepository = get()
        )
    }
}