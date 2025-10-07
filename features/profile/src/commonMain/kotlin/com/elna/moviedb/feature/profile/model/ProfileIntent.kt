package com.elna.moviedb.feature.profile.model

import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme

/**
 * Represents all possible user actions/intents in the Profile screen.
 * Following MVI pattern, these are the only ways users can interact with the ViewModel.
 */
sealed interface ProfileIntent {
    /**
     * User selected a new language
     */
    data class SetLanguage(val language: AppLanguage) : ProfileIntent

    /**
     * User selected a new theme
     */
    data class SetTheme(val theme: AppTheme) : ProfileIntent
}
