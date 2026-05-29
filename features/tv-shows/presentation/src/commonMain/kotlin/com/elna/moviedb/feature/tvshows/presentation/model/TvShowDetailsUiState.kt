package com.elna.moviedb.feature.tvshows.presentation.model

import com.elna.moviedb.core.model.DataError
import com.elna.moviedb.feature.tvshows.domain.model.TvShowDetails

sealed interface TvShowDetailsUiState {
    data object Loading : TvShowDetailsUiState
    data class Success(val tvShowDetails: TvShowDetails) : TvShowDetailsUiState
    data class Error(val error: DataError) : TvShowDetailsUiState
}
