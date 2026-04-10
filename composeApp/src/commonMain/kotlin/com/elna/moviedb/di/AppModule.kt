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
import com.elna.moviedb.reviews.ReviewsViewModel
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val reviewsModule = module {
    factory { (contentId: Int, isMovie: Boolean) ->
        ReviewsViewModel(
            contentId = contentId,
            isMovie = isMovie,
            moviesRepository = get(),
            tvShowsRepository = get()
        )
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()

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
        personModule,
        reviewsModule
    )
}

fun iOsInitKoin() {
    initKoin { }
}
