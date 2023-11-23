package com.example.moviedb.movies.di


import com.example.moviedb.movies.ui.movie_details.MovieDetailsViewModel
import com.example.moviedb.movies.ui.movies.MoviesViewModel
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