package com.example.moviedb.movies.di


import com.example.moviedb.data.movies.MoviesRepository
import com.example.moviedb.data.movies.MoviesRepositoryImpl
import com.example.moviedb.data.movies.data_sources.MoviesRemoteDataSource
import com.example.moviedb.movies.ui.movie_details.MovieDetailsViewModel
import com.example.moviedb.movies.ui.movies.MoviesViewModel
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