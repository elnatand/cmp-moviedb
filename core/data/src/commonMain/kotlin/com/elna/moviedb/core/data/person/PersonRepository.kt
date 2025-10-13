package com.elna.moviedb.core.data.person

import com.elna.moviedb.core.model.AppResult
import com.elna.moviedb.core.model.PersonDetails

interface PersonRepository {
    /**
 * Retrieves detailed information about a person identified by their numeric ID.
 *
 * @param personId The unique integer identifier of the person.
 * @return An AppResult containing the person's details on success or an error representation on failure.
 */
suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails>
}