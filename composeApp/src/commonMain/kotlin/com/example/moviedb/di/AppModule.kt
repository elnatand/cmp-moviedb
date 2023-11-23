package com.example.moviedb.di



import com.example.moviedb.profile.di.profileModule
import com.example.moviedb.tvshows.di.tvShowsModule
import com.example.moviedb.movies.di.moviesModule
import com.example.moviedb.data.di.dataModule
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
