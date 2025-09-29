package com.elna.moviedb.di



import com.elna.moviedb.core.common.di.commonModule
import com.elna.moviedb.feature.profile.di.profileModule
import com.elna.moviedb.feature.tvshows.di.tvShowsModule
import com.elna.moviedb.feature.movies.di.moviesModule
import com.elna.moviedb.core.data.di.dataModule
import com.elna.moviedb.core.database.di.databaseModule
import com.elna.moviedb.core.database.di.platformDatabaseModule
import com.elna.moviedb.core.network.di.networkModule
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
        networkModule,
        dataModule,
        databaseModule,
        moviesModule,
        tvShowsModule,
        profileModule
    )
}
