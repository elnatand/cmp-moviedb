package model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: String,
    val path: String
)