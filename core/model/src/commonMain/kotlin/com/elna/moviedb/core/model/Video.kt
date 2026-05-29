package com.elna.moviedb.core.model

enum class VideoSite(val displayName: String) {
    YOUTUBE("YouTube"),
    VIMEO("Vimeo"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(value: String): VideoSite {
            return when (value.lowercase()) {
                "youtube" -> YOUTUBE
                "vimeo" -> VIMEO
                else -> UNKNOWN
            }
        }
    }
}

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: VideoSite,
    val type: String,
    val official: Boolean,
    /**
     * ISO-8601 publish timestamp from TMDB. Carried through so the cached ordering
     * (official, then most recent) matches the freshly-fetched ordering. Nullable because
     * the API may omit it.
     */
    val publishedAt: String? = null
) {
    fun getThumbnailUrl(): String = when (site) {
        VideoSite.YOUTUBE -> "https://img.youtube.com/vi/$key/hqdefault.jpg"
        VideoSite.VIMEO -> "https://vumbnail.com/$key.jpg"
        else -> ""
    }

    fun getVideoUrl(): String = when (site) {
        VideoSite.YOUTUBE -> "https://www.youtube.com/watch?v=$key"
        VideoSite.VIMEO -> "https://vimeo.com/$key"
        else -> ""
    }
}
