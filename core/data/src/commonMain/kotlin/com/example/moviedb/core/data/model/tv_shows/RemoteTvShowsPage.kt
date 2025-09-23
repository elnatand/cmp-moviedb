package com.example.moviedb.core.data.model.tv_shows


import kotlinx.serialization.Serializable

@Serializable
data class RemoteTvShowsPage(
    val page: Int,
    val total_pages: Int,
    val results: List<NetworkTvShow>
)