package com.example.moviedb.di

fun initKoin() {
    initKoin(
        appDeclaration = {
            modules(iOSModule)
        }
    )
}