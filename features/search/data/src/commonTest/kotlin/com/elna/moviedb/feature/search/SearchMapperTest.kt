package com.elna.moviedb.feature.search

import com.elna.moviedb.feature.search.data.mappers.toSearchResult
import com.elna.moviedb.feature.search.data.mappers.toTmdbPath
import com.elna.moviedb.feature.search.data.model.RemoteMultiSearchItem
import com.elna.moviedb.feature.search.domain.model.SearchFilter
import com.elna.moviedb.feature.search.domain.model.SearchResultItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchMapperTest {

    @Test
    fun `each filter maps to its TMDB endpoint`() {
        assertEquals("/search/multi", SearchFilter.ALL.toTmdbPath())
        assertEquals("/search/movie", SearchFilter.MOVIES.toTmdbPath())
        assertEquals("/search/tv", SearchFilter.TV_SHOWS.toTmdbPath())
        assertEquals("/search/person", SearchFilter.PEOPLE.toTmdbPath())
    }

    @Test
    fun `multi-search dispatches on media_type to the matching item`() {
        val movie = RemoteMultiSearchItem(id = 1, mediaType = "movie", title = "Inception")
            .toSearchResult()
        val tv = RemoteMultiSearchItem(id = 2, mediaType = "tv", name = "Severance")
            .toSearchResult()
        val person = RemoteMultiSearchItem(id = 3, mediaType = "person", name = "Jodie Foster")
            .toSearchResult()

        assertTrue(movie is SearchResultItem.MovieItem)
        assertEquals("Inception", movie.title)
        assertTrue(tv is SearchResultItem.TvShowItem)
        assertEquals("Severance", tv.name)
        assertTrue(person is SearchResultItem.PersonItem)
        assertEquals("Jodie Foster", person.name)
    }

    @Test
    fun `multi-search returns null for an unknown media_type`() {
        assertNull(RemoteMultiSearchItem(id = 1, mediaType = "collection").toSearchResult())
        assertNull(RemoteMultiSearchItem(id = 1, mediaType = null).toSearchResult())
    }

    @Test
    fun `multi-search returns null when the title or name needed for the type is missing`() {
        // A movie row without a title, or a tv/person row without a name, can't be rendered.
        assertNull(RemoteMultiSearchItem(id = 1, mediaType = "movie", title = null).toSearchResult())
        assertNull(RemoteMultiSearchItem(id = 2, mediaType = "tv", name = null).toSearchResult())
        assertNull(RemoteMultiSearchItem(id = 3, mediaType = "person", name = null).toSearchResult())
    }
}
