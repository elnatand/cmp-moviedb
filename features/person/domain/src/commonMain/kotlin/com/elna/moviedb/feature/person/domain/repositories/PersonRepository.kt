package com.elna.moviedb.feature.person.domain.repositories

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.person.domain.model.PersonDetails

interface PersonRepository {
    suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails>
}