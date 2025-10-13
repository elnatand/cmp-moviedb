package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatchers
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
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

    /**
     * Fetches detailed information for a person from the TMDB API.
     *
     * @param personId The TMDB person identifier.
     * @param language The language code for localized results (for example, "en-US").
     * @return An AppResult containing a RemotePersonDetails on success, or an error result produced by the network call handling.
     */
    suspend fun getPersonDetails(personId: Int, language: String): AppResult<RemotePersonDetails> {
        return withContext(appDispatchers.io) {
            safeApiCall {
                httpClient.get("${TMDB_BASE_URL}person/$personId") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemotePersonDetails>()
            }
        }
    }
}