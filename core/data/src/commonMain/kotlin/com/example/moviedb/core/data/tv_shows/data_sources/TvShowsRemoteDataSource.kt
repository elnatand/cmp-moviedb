package com.example.moviedb.core.data.tv_shows.data_sources

import com.example.moviedb.core.data.model.API_KEY
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import com.example.moviedb.core.model.TvShow
import com.example.moviedb.core.model.TvShowDetails
import com.example.moviedb.core.model.TvShowsPage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TvShowsRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getTvShowPage(page: Int): List<TvShow> {
        val tvShowsPages = httpClient
            .get("${TMDB_BASE_URL}tv/popular?api_key=$API_KEY") {
                url {
                    parameters.append("page", page.toString())
                }
            }
            .body<TvShowsPage>()
        return tvShowsPages.results
    }

    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails {

        return httpClient
            .get("${TMDB_BASE_URL}tv/${tvShowId}?api_key=$API_KEY")
            .body()
    }
}