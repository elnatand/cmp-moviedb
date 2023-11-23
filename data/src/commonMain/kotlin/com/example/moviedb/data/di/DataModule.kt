package com.example.moviedb.data.di

import com.example.moviedb.data.network.createHttpClient
import org.koin.dsl.module

val dataModule = module {
    single { createHttpClient(httpClientEngine = get()) }
}