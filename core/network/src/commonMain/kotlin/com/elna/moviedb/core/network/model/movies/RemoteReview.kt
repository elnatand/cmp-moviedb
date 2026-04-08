package com.elna.moviedb.core.network.model.movies

import com.elna.moviedb.core.model.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteReviewResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("results")
    val results: List<RemoteReview>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

@Serializable
data class RemoteReview(
    @SerialName("id")
    val id: String,
    @SerialName("author")
    val author: String,
    @SerialName("author_details")
    val authorDetails: RemoteReviewAuthorDetails?,
    @SerialName("content")
    val content: String,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class RemoteReviewAuthorDetails(
    @SerialName("name")
    val name: String?,
    @SerialName("username")
    val username: String?,
    @SerialName("avatar_path")
    val avatarPath: String?,
    @SerialName("rating")
    val rating: Double?
)

fun RemoteReview.toDomain() = Review(
    id = id,
    author = author,
    avatarPath = authorDetails?.avatarPath,
    rating = authorDetails?.rating,
    content = content,
    createdAt = createdAt
)
