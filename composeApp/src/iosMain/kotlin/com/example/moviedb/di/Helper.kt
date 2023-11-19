package com.example.moviedb.di

fun initKoin() {
    com.example.moviedb.di.initKoin(
        appDeclaration = {
            modules(iOSModule)
        }
    )
}