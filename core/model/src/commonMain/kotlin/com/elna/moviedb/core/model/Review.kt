package com.elna.moviedb.core.model

data class Review(
    val id: String,
    val author: String,
    val avatarPath: String?,
    val rating: Double?,
    val content: String,
    val createdAt: String
)
