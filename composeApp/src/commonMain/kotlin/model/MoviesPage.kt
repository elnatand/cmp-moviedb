package model

import kotlinx.serialization.Serializable
import model.Movie

@Serializable
data class MoviesPage(
    val page: Int,
    val total_pages: Int,
    val results: List<Movie>
)