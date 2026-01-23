package com.elna.moviedb.feature.profile.model

import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.ThemeConfig

/**
 * Represents all possible user actions/events in the Profile screen.
 * Following Android's unidirectional data flow pattern, these are the only ways
 * users can interact with the ViewModel.
 */
sealed interface ProfileEvent {
    /**
     * User selected a new language
     */
    data class SetLanguage(val language: AppLanguage) : ProfileEvent

    /**
     * User selected a new theme
     */
    data class SetTheme(val theme: ThemeConfig) : ProfileEvent
}
