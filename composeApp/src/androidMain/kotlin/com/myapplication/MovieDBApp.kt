package com.myapplication

import android.app.Application
import com.di.androidModule
import di.initKoin
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
