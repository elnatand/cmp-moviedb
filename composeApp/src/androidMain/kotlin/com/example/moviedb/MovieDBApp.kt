package com.example.moviedb

import android.app.Application
import com.example.moviedb.di.androidModule
import com.example.moviedb.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MovieDBApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(
            appDeclaration = {
                androidLogger()
                androidContext(this@MovieDBApp)
                modules(androidModule)
            }
        )
    }
}
