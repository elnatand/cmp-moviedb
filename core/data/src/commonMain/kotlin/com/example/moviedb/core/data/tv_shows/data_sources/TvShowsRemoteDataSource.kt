package com.example.moviedb.core.data.tv_shows.data_sources

import com.example.moviedb.core.common.AppDispatcher
import com.example.moviedb.core.data.model.TMDB_API_KEY
import com.example.moviedb.core.data.model.TMDB_BASE_URL
import com.example.moviedb.core.data.model.tv_shows.RemoteTvShow
import com.example.moviedb.core.data.model.tv_shows.RemoteTvShowDetails
import com.example.moviedb.core.data.model.tv_shows.RemoteTvShowsPage
import com.example.moviedb.core.model.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.withContext

class TvShowsRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatcher: AppDispatcher
) {
    suspend fun getTvShowsPage(page: Int): AppResult<RemoteTvShowsPage> {
        return try {
            val tvShowsPage = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}tv/popular?api_key=$TMDB_API_KEY") {
                        url { parameters.append("page", page.toString()) }
                    }.body<RemoteTvShowsPage>()
            }
            AppResult.Success(tvShowsPage)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getTvShowDetails(tvShowId: Int): RemoteTvShowDetails {
        return withContext(appDispatcher.getDispatcher()) {
            val httpResponse = httpClient
                .get("${TMDB_BASE_URL}tv/${tvShowId}?api_key=$TMDB_API_KEY")

            val jsonBody = httpResponse.bodyAsText()
            println("getTvShowDetails JSON response for ID $tvShowId:")
            println(jsonBody)

            httpResponse.body<RemoteTvShowDetails>()
        }
    }
}
