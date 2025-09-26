package com.example.moviedb.core.data.model.movies

import com.example.moviedb.core.database.model.MovieEntity
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class RemoteMovie(
    val id: Int,
    val title: String,
    val poster_path: String?,
)

@OptIn(ExperimentalTime::class)
fun RemoteMovie.asEntity(page: Int) = MovieEntity(
    id = id,
    timestamp = Clock.System.now().epochSeconds,
    page = page,
    title = title,
    poster_path = poster_path
)