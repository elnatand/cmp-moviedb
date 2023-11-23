package com.example.moviedb.di



import com.example.moviedb.feature.profile.di.profileModule
import com.example.moviedb.feature.tvshows.di.tvShowsModule
import com.example.moviedb.feature.movies.di.moviesModule
import com.example.moviedb.core.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()

    /**
     * Shared Modules
     */
    modules(
        dataModule,
        moviesModule,
        tvShowsModule,
        profileModule
    )
}
