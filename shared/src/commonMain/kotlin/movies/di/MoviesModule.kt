package movies.di

import movies.data.MoviesRepository
import movies.data.MoviesRepositoryImpl
import movies.data.data_sources.MoviesRemoteDataSource
import org.koin.dsl.module

val moviesModule = module {
    single { MoviesRemoteDataSource() }
    single<MoviesRepository> { MoviesRepositoryImpl(moviesRemoteDataSource = get()) }
}