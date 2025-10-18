package com.elna.moviedb.core.data.person

import com.elna.moviedb.core.data.util.LanguageProvider
import com.elna.moviedb.core.data.util.toFullImageUrl
import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.model.map
import com.elna.moviedb.core.network.PersonRemoteDataSource
import com.elna.moviedb.core.network.model.person.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PersonRepositoryImpl(
    private val personRemoteDataSource: PersonRemoteDataSource,
    private val languageProvider: LanguageProvider
) : PersonRepository {

    override suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails> = coroutineScope {
        val language = languageProvider.getCurrentLanguage()

        val personDetailsDeferred = async { personRemoteDataSource.getPersonDetails(personId, language) }
        val creditsDeferred = async { personRemoteDataSource.getCombinedCredits(personId, language) }

        val personDetailsResult = personDetailsDeferred.await()
        val creditsResult = creditsDeferred.await()

        return@coroutineScope personDetailsResult.map { remotePersonDetails ->
            val filmography = when (creditsResult) {
                is AppResult.Success -> creditsResult.data.toDomain().map { credit ->
                    credit.copy(posterPath = credit.posterPath.toFullImageUrl())
                }
                is AppResult.Error -> emptyList()
            }

            remotePersonDetails.toDomain().copy(
                profilePath = remotePersonDetails.profilePath.toFullImageUrl(),
                filmography = filmography
            )
        }
    }
}
