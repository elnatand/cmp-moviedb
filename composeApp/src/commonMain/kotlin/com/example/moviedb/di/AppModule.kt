package com.example.moviedb.di



import com.example.moviedb.core.common.di.commonModule
import com.example.moviedb.feature.profile.di.profileModule
import com.example.moviedb.feature.tvshows.di.tvShowsModule
import com.example.moviedb.feature.movies.di.moviesModule
import com.example.moviedb.core.data.di.dataModule
import com.example.moviedb.core.database.di.databaseModule
import com.example.moviedb.core.database.di.platformDatabaseModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()

    /**
     * Shared Modules
     */
    modules(
        platformDatabaseModule(),
        commonModule,
        dataModule,
        databaseModule,
        moviesModule,
        tvShowsModule,
        profileModule
    )
}
