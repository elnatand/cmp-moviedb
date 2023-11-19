package com.example.moviedb.data.tv_shows.data_sources

import com.example.moviedb.model.TvShow
import com.example.moviedb.model.TvShowDetails
import com.example.moviedb.model.TvShowsPage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TvShowsRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getTvShowPage(page: Int): List<TvShow> {
        val tvShowsPages = httpClient
            .get("https://api.themoviedb.org/3/tv/popular?api_key=fe3e15709f26d5df026b17a743dbd529") {
                url {
                    parameters.append("page", page.toString())
                }
            }
            .body<TvShowsPage>()
        return tvShowsPages.results
    }

    suspend fun getTvShowDetails(tvShowId: Int): TvShowDetails {
        return httpClient
            .get("https://api.themoviedb.org/3/tv/${tvShowId}?api_key=fe3e15709f26d5df026b17a743dbd529")
            .body()
    }
}