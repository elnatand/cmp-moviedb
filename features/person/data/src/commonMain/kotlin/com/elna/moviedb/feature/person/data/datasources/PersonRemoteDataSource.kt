package com.elna.moviedb.feature.person.data.datasources

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.TmdbApiClient
import com.elna.moviedb.feature.person.data.model.RemoteCombinedCredits
import com.elna.moviedb.feature.person.data.model.RemotePersonDetails

class PersonRemoteDataSource(
    private val apiClient: TmdbApiClient
) {

    suspend fun getPersonDetails(personId: Int, language: String): AppResult<RemotePersonDetails> {
        return apiClient.get(
            path = "/person/$personId",
            "language" to language
        )
    }

    suspend fun getCombinedCredits(personId: Int, language: String): AppResult<RemoteCombinedCredits> {
        return apiClient.get(
            path = "/person/$personId/combined_credits",
            "language" to language
        )
    }
}
