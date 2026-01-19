package com.elna.moviedb.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elna.moviedb.feature.movies.model.MovieCategory

@Entity
data class MovieEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val title: String,
    val posterPath: String?,
    val category: String = MovieCategory.POPULAR.name
)