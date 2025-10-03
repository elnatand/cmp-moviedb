package com.elna.moviedb.feature.profile.di

import com.elna.moviedb.feature.profile.ui.ProfileViewModel
import org.koin.dsl.module

val profileModule = module {
    factory {
        ProfileViewModel(get())
    }
}