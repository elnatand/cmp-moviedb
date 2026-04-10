package com.elna.moviedb.core.model

data class ReviewsPage(
    val reviews: List<Review>,
    val page: Int,
    val totalPages: Int
)
