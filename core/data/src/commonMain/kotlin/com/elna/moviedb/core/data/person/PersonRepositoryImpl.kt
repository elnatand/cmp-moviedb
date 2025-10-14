package com.elna.moviedb.core.data.person

import com.elna.moviedb.core.datastore.PreferencesManager
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.model.map
import com.elna.moviedb.core.network.PersonRemoteDataSource
import com.elna.moviedb.core.network.model.person.toDomain
import kotlinx.coroutines.flow.first

class PersonRepositoryImpl(
    private val personRemoteDataSource: PersonRemoteDataSource,
    private val preferencesManager: PreferencesManager
) : PersonRepository {

    override suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails> {
        val language = getLanguage()

        val personDetailsResult = personRemoteDataSource.getPersonDetails(personId, language)
        val creditsResult = personRemoteDataSource.getCombinedCredits(personId, language)

        return personDetailsResult.map { remotePersonDetails ->
            val filmography = when (creditsResult) {
                is AppResult.Success -> creditsResult.data.toDomain()
                is AppResult.Error -> emptyList()
            }

            remotePersonDetails.toDomain().copy(filmography = filmography)
        }
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}