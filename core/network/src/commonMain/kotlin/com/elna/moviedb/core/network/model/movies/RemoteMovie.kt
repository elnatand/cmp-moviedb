package com.elna.moviedb.core.network.model.movies

import com.elna.moviedb.core.database.model.MovieEntity
import com.elna.moviedb.core.network.model.TMDB_IMAGE_URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class RemoteMovie(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("poster_path")
    val posterPath: String?,
)

@OptIn(ExperimentalTime::class)
fun RemoteMovie.asEntity(page: Int) = MovieEntity(
    id = id,
    timestamp = Clock.System.now().epochSeconds,
    page = page,
    title = title,
    poster_path = "$TMDB_IMAGE_URL$posterPath"
)