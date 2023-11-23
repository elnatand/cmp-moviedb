package com.example.moviedb.profile.di

import com.example.moviedb.profile.ui.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
    factory {
        ProfileViewModel()
    }
}