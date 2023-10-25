package di

import features.movies.di.moviesModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.compose.KoinApplication

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {



}
