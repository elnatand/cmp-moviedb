package com.elna.moviedb.core.database.model

import androidx.room.Entity


/**
 * A cached movie row, scoped to a single category.
 *
 * The primary key is composite (`id` + `category`) on purpose: TMDB's popular / top_rated /
 * now_playing lists overlap heavily, so the same movie id can legitimately appear in several
 * categories at once. A single-column `id` key would let an insert for one category REPLACE
 * (and thus erase) the row for another, making the movie silently vanish from the first list.
 *
 * [position] preserves the API's ranking order within a category: the list query orders by it
 * rather than by an insertion timestamp, which collided at second granularity within a page.
 */
@Entity(primaryKeys = ["id", "category"])
data class MovieEntity(
    val id: Int,
    val position: Int,
    val title: String,
    val posterPath: String?,
    val category: String
)
