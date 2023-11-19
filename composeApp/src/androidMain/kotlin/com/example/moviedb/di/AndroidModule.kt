package com.example.moviedb.di


import io.ktor.client.engine.android.Android
import org.koin.dsl.module

/**
 * Android platform specific module
 */
val androidModule = module {
    single { Android.create() }  //for inject httpClientEngine
}
