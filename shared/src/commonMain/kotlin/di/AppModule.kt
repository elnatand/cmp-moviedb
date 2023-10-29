package di


import features.movies.di.moviesModule
import features.tv_shows.di.tvShowsModule
import network.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()

    /**
     * Shared Modules
     */
    modules(
        dataModule, moviesModule, tvShowsModule
    )
}
