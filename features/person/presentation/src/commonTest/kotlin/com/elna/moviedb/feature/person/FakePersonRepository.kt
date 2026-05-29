package com.elna.moviedb.feature.person

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.feature.person.domain.model.PersonDetails
import com.elna.moviedb.feature.person.domain.repositories.PersonRepository
import kotlinx.coroutines.delay

/**
 * Fake [PersonRepository] for testing. Records every [getPersonDetails] call and returns a
 * configurable result, with an optional delay to keep a call "in flight".
 */
class FakePersonRepository : PersonRepository {

    var callCount = 0
    var delayMillis = 0L
    var result: AppResult<PersonDetails> = AppResult.Error("Not configured")

    override suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails> {
        callCount++
        if (delayMillis > 0) delay(delayMillis)
        return result
    }
}
