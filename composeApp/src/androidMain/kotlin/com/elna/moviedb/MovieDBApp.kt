package com.elna.moviedb

import android.app.Application
import com.elna.moviedb.core.network.di.androidDataModule
import com.elna.moviedb.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MovieDBApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(
            appDeclaration = {
                androidLogger()
                androidContext(this@MovieDBApp)
                modules(androidDataModule)
            }
        )
    }
}
