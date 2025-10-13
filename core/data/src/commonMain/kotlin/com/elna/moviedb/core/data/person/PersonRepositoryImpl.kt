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

    /**
     * Fetches details for the specified person and returns them as a domain model wrapped in an `AppResult`.
     *
     * @param personId The unique identifier of the person to retrieve.
     * @return `AppResult` containing the `PersonDetails` on success, an error result otherwise.
     */
    override suspend fun getPersonDetails(personId: Int): AppResult<PersonDetails> {
        return personRemoteDataSource.getPersonDetails(personId, getLanguage()).map {
            it.toDomain()
        }
    }

    /**
     * Constructs the language tag used for remote requests from the stored app language.
     *
     * Reads the saved language code and resolves its country code, then combines them into a tag
     * in the format `languageCode-countryCode` (for example, `en-US`).
     *
     * @return A language tag in the format `languageCode-countryCode`.
     */
    private suspend fun getLanguage(): String {
        val languageCode = preferencesManager.getAppLanguageCode().first()
        val countryCode = AppLanguage.getAppLanguageByCode(languageCode).countryCode
        return "$languageCode-$countryCode"
    }
}