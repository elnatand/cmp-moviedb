package com.elna.moviedb.di


import com.elna.moviedb.core.common.di.commonModule
import com.elna.moviedb.feature.person.presentation.di.personPresentationModule
import com.elna.moviedb.feature.profile.presentation.di.profileModule
import com.elna.moviedb.feature.search.presentation.di.searchPresentationModule
import com.elna.moviedb.feature.tvshows.data.di.tvShowsDataModule
import com.elna.moviedb.feature.movies.di.moviesPresentationModule
import com.elna.moviedb.core.data.di.dataModule
import com.elna.moviedb.core.database.di.databaseModule
import com.elna.moviedb.core.datastore.di.dataStoreModule
import com.elna.moviedb.core.network.di.networkModule
import com.elna.moviedb.feature.movies.di.moviesDataModule
import com.elna.moviedb.feature.person.data.di.personDataModule
import com.elna.moviedb.feature.search.data.di.searchDataModule
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

        moviesPresentationModule,
        moviesDataModule,

        tvShowsDataModule,
        _root_ide_package_.com.elna.moviedb.feature.tvshows.presentation.di.tvShowsPresentationModule,

        searchPresentationModule,
        searchDataModule,

        personPresentationModule,
        personDataModule,

        profileModule,
    )
}
