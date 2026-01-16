package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.person.RemoteCombinedCredits
import com.elna.moviedb.core.network.model.person.RemotePersonDetails
import com.elna.moviedb.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.withContext

class PersonRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDispatchers: AppDispatchers
) {

    suspend fun getPersonDetails(personId: Int, language: String): AppResult<RemotePersonDetails> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/person/$personId") {
                    url {
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemotePersonDetails>()
            }
        }
    }

    suspend fun getCombinedCredits(personId: Int, language: String): AppResult<RemoteCombinedCredits> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}/person/$personId/combined_credits") {
                    url {
                        parameters.append("api_key", BuildKonfig.TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemoteCombinedCredits>()
            }
        }
    }
}
