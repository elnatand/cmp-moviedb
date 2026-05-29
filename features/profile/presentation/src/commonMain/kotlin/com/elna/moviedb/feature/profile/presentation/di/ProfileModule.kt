package com.elna.moviedb.feature.profile.presentation.di

import com.elna.moviedb.feature.profile.presentation.ui.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
    factory {
        ProfileViewModel(get(), get())
    }
}