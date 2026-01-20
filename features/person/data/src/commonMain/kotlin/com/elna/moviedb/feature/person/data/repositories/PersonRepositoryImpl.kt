package com.elna.moviedb.feature.person.data.repositories

import com.elna.moviedb.core.datastore.language.LanguageProvider
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.map
import com.elna.moviedb.feature.person.data.datasources.PersonRemoteDataSource
import com.elna.moviedb.feature.person.data.mappar.toDomain
import com.elna.moviedb.feature.person.domain.model.PersonDetails
import com.elna.moviedb.feature.person.domain.repositories.PersonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PersonRepositoryImpl(
    private val personRemoteDataSource: PersonRemoteDataSource,
    private val languageProvider: LanguageProvider
) : PersonRepository {

    override suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails> =
        coroutineScope {
            val language = languageProvider.getCurrentLanguage()

            val personDetailsDeferred =
                async { personRemoteDataSource.getPersonDetails(personId, language) }
            val creditsDeferred =
                async { personRemoteDataSource.getCombinedCredits(personId, language) }

            val personDetailsResult = personDetailsDeferred.await()
            val creditsResult = creditsDeferred.await()

            return@coroutineScope personDetailsResult.map { remotePersonDetails ->
                val filmography = when (creditsResult) {
                    is AppResult.Success -> creditsResult.data.toDomain()
                    is AppResult.Error -> emptyList()
                }

                remotePersonDetails.toDomain().copy(
                    filmography = filmography
                )
            }
        }
}