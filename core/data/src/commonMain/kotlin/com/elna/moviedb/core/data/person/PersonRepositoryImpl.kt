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
        return personRemoteDataSource.getPersonDetails(personId, getLanguage()).map {
            it.toDomain()
        }
    }

    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}