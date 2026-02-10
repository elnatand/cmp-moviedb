package com.elna.moviedb.feature.movies.di


import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.feature.movies.navigation.MoviesNavigationFactory
import com.elna.moviedb.feature.movies.ui.movie_details.MovieDetailsViewModel
import com.elna.moviedb.feature.movies.ui.movies.MoviesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val moviesModule = module {

    factoryOf(::MoviesViewModel)

    factory { (id: Int) ->
        MovieDetailsViewModel(
            movieId = id,
            moviesRepository = get()
        )
    }

    singleOf(::MoviesNavigationFactory) bind NavigationFactory::class
}
