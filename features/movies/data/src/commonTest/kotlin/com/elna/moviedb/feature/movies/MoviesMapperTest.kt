package com.elna.moviedb.feature.movies

import com.elna.moviedb.feature.movies.mappers.asEntity
import com.elna.moviedb.feature.movies.mappers.toDomain
import com.elna.moviedb.feature.movies.mappers.toTmdbPath
import com.elna.moviedb.feature.movies.model.MovieCategory
import com.elna.moviedb.feature.movies.model.MovieDetails
import com.elna.moviedb.feature.movies.model.RemoteMovie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoviesMapperTest {

    @Test
    fun `RemoteMovie asEntity carries position and category`() {
        val entity = RemoteMovie(id = 7, title = "Dune", posterPath = "/p.jpg")
            .asEntity(MovieCategory.TOP_RATED, position = 2042)

        assertEquals(7, entity.id)
        assertEquals("Dune", entity.title)
        assertEquals("/p.jpg", entity.posterPath)
        assertEquals(2042, entity.position)
        assertEquals(MovieCategory.TOP_RATED.name, entity.category)
    }

    @Test
    fun `toTmdbPath maps every category`() {
        assertEquals("/movie/popular", MovieCategory.POPULAR.toTmdbPath())
        assertEquals("/movie/top_rated", MovieCategory.TOP_RATED.toTmdbPath())
        assertEquals("/movie/now_playing", MovieCategory.NOW_PLAYING.toTmdbPath())
    }

    @Test
    fun `MovieDetails list fields survive the entity round-trip even with commas in values`() {
        // The Unit Separator delimiter (not a comma) must preserve values that contain commas.
        val details = movieDetails(
            genres = listOf("Action", "Sci-Fi"),
            productionCompanies = listOf("Columbia Pictures Corporation, Ltd.", "Marvel"),
        )

        val roundTripped = details.asEntity().toDomain()

        assertEquals(listOf("Action", "Sci-Fi"), roundTripped.genres)
        assertEquals(
            listOf("Columbia Pictures Corporation, Ltd.", "Marvel"),
            roundTripped.productionCompanies
        )
    }

    @Test
    fun `null list fields round-trip back to null`() {
        val roundTripped = movieDetails(genres = null, productionCompanies = null)
            .asEntity()
            .toDomain()

        assertNull(roundTripped.genres)
        assertNull(roundTripped.productionCompanies)
    }

    @Test
    fun `scalar fields survive the entity round-trip`() {
        val details = movieDetails(genres = null, productionCompanies = null)
            .copy(id = 99, title = "Interstellar", voteAverage = 8.6)

        val roundTripped = details.asEntity().toDomain()

        assertEquals(99, roundTripped.id)
        assertEquals("Interstellar", roundTripped.title)
        assertEquals(8.6, roundTripped.voteAverage)
    }

    private fun movieDetails(
        genres: List<String>?,
        productionCompanies: List<String>?,
    ) = MovieDetails(
        id = 1,
        title = "Title",
        overview = "Overview",
        posterPath = null,
        backdropPath = null,
        releaseDate = null,
        runtime = null,
        voteAverage = null,
        voteCount = null,
        adult = null,
        budget = null,
        revenue = null,
        homepage = null,
        imdbId = null,
        originalLanguage = null,
        originalTitle = null,
        popularity = null,
        status = null,
        tagline = null,
        genres = genres,
        productionCompanies = productionCompanies,
        productionCountries = null,
        spokenLanguages = null,
    )
}
