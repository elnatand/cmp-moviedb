package com.example.moviedb.feature.profile.di

import com.example.moviedb.feature.profile.ui.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
    factory {
        ProfileViewModel()
    }
}