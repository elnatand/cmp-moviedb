package com.elna.moviedb.core.data.tv_shows.data_sources

import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.data.model.TMDB_API_KEY
import com.elna.moviedb.core.data.model.TMDB_BASE_URL
import com.elna.moviedb.core.data.model.platformCountry
import com.elna.moviedb.core.data.model.platformLanguage
import com.elna.moviedb.core.data.model.tv_shows.RemoteTvShowDetails
import com.elna.moviedb.core.data.model.tv_shows.RemoteTvShowsPage
import com.elna.moviedb.core.model.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

class TvShowsRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatcher: AppDispatcher
) {
    private val language = "$platformLanguage-$platformCountry"
    suspend fun getPopularTvShowsPage(page: Int): AppResult<RemoteTvShowsPage> {
        return try {
            val tvShowsPage = withContext(appDispatcher.getDispatcher()) {
                httpClient
                    .get("${TMDB_BASE_URL}tv/popular") {
                        url {
                            parameters.append("api_key", TMDB_API_KEY)
                            parameters.append("page", page.toString())
                            parameters.append("language", language)
                        }
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
                .get("${TMDB_BASE_URL}tv/${tvShowId}") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }

            httpResponse.body<RemoteTvShowDetails>()
        }
    }
}
