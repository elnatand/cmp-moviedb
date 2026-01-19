package com.elna.moviedb.feature.search.presentation.di

import com.elna.moviedb.feature.search.presentation.ui.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchPresentationModule = module {
    viewModel {
        SearchViewModel(
            searchRepository = get()
        )
    }
}