package com.example.moviedb.feature.movies.di


import com.example.moviedb.feature.movies.ui.movie_details.MovieDetailsComponent
import com.example.moviedb.feature.movies.ui.movies.MoviesComponent
import org.koin.dsl.module

val moviesModule = module {

    factory {
        MoviesComponent(
            moviesRepository = get(),
        )
    }

    factory { (id: Int) ->
        MovieDetailsComponent(
            movieId = id,
            moviesRepository = get()
        )
    }
}