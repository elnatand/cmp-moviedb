package com.elna.moviedb.di

fun initKoin() {
    initKoin(
        appDeclaration = {
            modules(iOSModule)
        }
    )
}