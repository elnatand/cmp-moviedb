package com.elna.moviedb.core.network

import com.elna.moviedb.core.common.AppDispatcher
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.network.model.TMDB_API_KEY
import com.elna.moviedb.core.network.model.TMDB_BASE_URL
import com.elna.moviedb.core.network.model.person.RemotePersonDetails
import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PersonRemoteDataSource(
    private val httpClient: HttpClient,
    private val preferencesManager: PreferencesManager,
    private val appDispatcher: AppDispatcher
) {

    suspend fun getPersonDetails(personId: Int): AppResult<RemotePersonDetails> {
        return try {
            val personDetails = withContext(appDispatcher.getDispatcher()) {
                val language = getLanguage()
                httpClient.get("${TMDB_BASE_URL}person/$personId") {
                    url {
                        parameters.append("api_key", TMDB_API_KEY)
                        parameters.append("language", language)
                    }
                }.body<RemotePersonDetails>()
            }
            AppResult.Success(personDetails)
        } catch (e: Exception) {
            AppResult.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }

    suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}