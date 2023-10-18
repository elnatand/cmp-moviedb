package movies.di

import movies.data.MoviesRepository
import movies.data.MoviesRepositoryImpl
import movies.data.data_sources.MoviesRemoteDataSource
import network.createHttpClient
import org.koin.dsl.module

val moviesModule = module {
    single { createHttpClient(httpClientEngine = get()) }
    single { MoviesRemoteDataSource(httpClient = get()) }
    single<MoviesRepository> { MoviesRepositoryImpl(moviesRemoteDataSource = get()) }
}