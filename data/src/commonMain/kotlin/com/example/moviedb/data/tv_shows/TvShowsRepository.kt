package com.example.moviedb.data.tv_shows

import com.example.moviedb.model.TvShow
import com.example.moviedb.model.TvShowDetails

interface TvShowsRepository {
    suspend fun getTvShowsPage(page: Int): List<TvShow>
    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails
}