package com.elna.moviedb.di


import com.elna.moviedb.core.common.di.commonModule
import com.elna.moviedb.core.data.di.dataModule
import com.elna.moviedb.core.database.di.databaseModule
import com.elna.moviedb.core.datastore.di.dataStoreModule
import com.elna.moviedb.core.network.di.networkModule
import com.elna.moviedb.feature.movies.di.moviesModule
import com.elna.moviedb.feature.person.di.personModule
import com.elna.moviedb.feature.profile.di.profileModule
import com.elna.moviedb.feature.search.di.searchModule
import com.elna.moviedb.feature.tvshows.di.tvShowsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()

    /**
     * Shared Modules
     */
    modules(
        commonModule,
        networkModule,
        dataModule,
        databaseModule,
        dataStoreModule,
        moviesModule,
        tvShowsModule,
        searchModule,
        profileModule,
        personModule
    )
}

fun iOsInitKoin() {
    initKoin { }
}
