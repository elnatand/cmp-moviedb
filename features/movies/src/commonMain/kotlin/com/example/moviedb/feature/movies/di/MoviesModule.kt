package com.example.moviedb.feature.movies.di


import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsViewModel
import com.example.moviedb.feature.movies.ui.movies.MoviesViewModel
import org.koin.dsl.module

val moviesModule = module {

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