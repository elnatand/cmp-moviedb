package com.example.moviedb.network

import org.koin.dsl.module

val dataModule = module {
    single { createHttpClient(httpClientEngine = get()) }
}