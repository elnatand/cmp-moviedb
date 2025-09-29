package com.elna.moviedb.di


import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

val iOSModule = module {
    single { Darwin.create() } //for inject httpClientEngine
}
