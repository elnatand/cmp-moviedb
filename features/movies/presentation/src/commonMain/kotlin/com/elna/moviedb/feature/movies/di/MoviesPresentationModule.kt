package com.elna.moviedb.feature.movies.di


import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsViewModel
import com.elna.moviedb.feature.movies.ui.movies.MoviesViewModel
import org.koin.dsl.module

val moviesPresentationModule = module {

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