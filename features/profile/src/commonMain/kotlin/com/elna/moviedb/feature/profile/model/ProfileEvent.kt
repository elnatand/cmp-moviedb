package com.elna.moviedb.feature.profile.model

import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme

sealed interface ProfileEvent {
    data class SetLanguage(val language: AppLanguage) : ProfileEvent
    data class SetTheme(val theme: AppTheme) : ProfileEvent
}
