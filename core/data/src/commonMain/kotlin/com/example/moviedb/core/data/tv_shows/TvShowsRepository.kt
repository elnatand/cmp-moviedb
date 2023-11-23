package com.example.moviedb.core.data.tv_shows

import com.example.moviedb.core.model.TvShow
import com.example.moviedb.core.model.TvShowDetails

interface TvShowsRepository {
    suspend fun getTvShowsPage(page: Int): List<TvShow>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
}