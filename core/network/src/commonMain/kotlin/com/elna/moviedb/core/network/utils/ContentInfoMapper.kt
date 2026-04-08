package com.elna.moviedb.core.network.utils

import com.elna.moviedb.core.model.ContentInfo
import com.elna.moviedb.core.network.model.movies.RemoteReleaseDatesResponse
import com.elna.moviedb.core.network.model.tv_shows.RemoteContentRatingsResponse

private val VIOLENCE_KEYWORDS = setOf(
    "violence", "gore", "blood", "brutal", "brutality", "murder", "fight", "torture",
    "war", "weapon", "gun", "stabbing", "shooting", "killing", "dead"
)

private val SEXUAL_CONTENT_KEYWORDS = setOf(
    "nudity", "sex", "sexual", "erotic", "erotica", "explicit", "intimacy", "nude"
)

private val DRUG_KEYWORDS = setOf(
    "drug", "drugs", "cocaine", "heroin", "marijuana", "cannabis", "alcohol",
    "drinking", "substance abuse", "addiction", "overdose", "smoking"
)

private val SELF_HARM_KEYWORDS = setOf(
    "suicide", "self-harm", "self harm", "self-destruction"
)

private val STRONG_LANGUAGE_KEYWORDS = setOf(
    "profanity", "strong language", "crude language", "vulgarity"
)

fun List<String>.toContentDescriptors(): List<String> {
    val descriptors = mutableListOf<String>()
    val lowercased = map { it.lowercase() }

    if (lowercased.any { kw -> VIOLENCE_KEYWORDS.any { pattern -> kw.contains(pattern) } }) {
        descriptors.add("Violence")
    }
    if (lowercased.any { kw -> SEXUAL_CONTENT_KEYWORDS.any { pattern -> kw.contains(pattern) } }) {
        descriptors.add("Sexual Content")
    }
    if (lowercased.any { kw -> DRUG_KEYWORDS.any { pattern -> kw.contains(pattern) } }) {
        descriptors.add("Drugs & Alcohol")
    }
    if (lowercased.any { kw -> SELF_HARM_KEYWORDS.any { pattern -> kw.contains(pattern) } }) {
        descriptors.add("Self-Harm")
    }
    if (lowercased.any { kw -> STRONG_LANGUAGE_KEYWORDS.any { pattern -> kw.contains(pattern) } }) {
        descriptors.add("Strong Language")
    }

    return descriptors
}

fun RemoteReleaseDatesResponse.extractUsRating(): String? {
    val usResult = results.firstOrNull { it.iso31661 == "US" } ?: results.firstOrNull()
    // Type 3 = Theatrical release; prefer it, fall back to any
    return usResult?.releaseDates
        ?.sortedByDescending { it.type == 3 }
        ?.firstOrNull { it.certification.isNotBlank() }
        ?.certification
}

fun RemoteContentRatingsResponse.extractUsRating(): String? {
    return results.firstOrNull { it.iso31661 == "US" }?.rating?.takeIf { it.isNotBlank() }
        ?: results.firstOrNull { it.rating.isNotBlank() }?.rating
}
