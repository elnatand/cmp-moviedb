package com.elna.moviedb.core.data.person

import com.elna.moviedb.core.model.PersonDetails

interface PersonRepository {
    suspend fun getPersonDetails(personId: Int): PersonDetails
}