package com.elna.moviedb.core.data

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.data.movies.MoviesRepository
import com.elna.moviedb.core.data.tv_shows.TvShowsRepository
import com.elna.moviedb.core.datastore.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Initializer that creates and manages language change coordinators for all repositories.
 * This class is instantiated by Koin on app startup to ensure coordinators begin
 * observing language changes immediately.
 */
class LanguageChangeCoordinatorsInitializer(
    preferencesManager: PreferencesManager,
    appDispatchers: AppDispatchers,
    moviesRepository: MoviesRepository,
    tvShowsRepository: TvShowsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + appDispatchers.main)

    private val moviesCoordinator = LanguageChangeCoordinator(
        preferencesManager = preferencesManager,
        scope = scope,
        onLanguageChange = { moviesRepository.clearAndReload() }
    )

    private val tvShowsCoordinator = LanguageChangeCoordinator(
        preferencesManager = preferencesManager,
        scope = scope,
        onLanguageChange = { tvShowsRepository.clearAndReload() }
    )
}
