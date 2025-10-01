package com.elna.moviedb.core.data.person

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.PersonDetails
import com.elna.moviedb.core.network.PersonRemoteDataSource
import com.elna.moviedb.core.network.model.person.toDomain

class PersonRepositoryImpl(
    private val personRemoteDataSource: PersonRemoteDataSource
) : PersonRepository {

    override suspend fun getPersonDetails(personId: Int): PersonDetails {
        return when (val result = personRemoteDataSource.getPersonDetails(personId)) {
            is AppResult.Success -> {
                result.data.toDomain()
            }
            is AppResult.Error -> {
                throw Exception(result.message)
            }
        }
    }
}