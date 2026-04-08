package com.elna.moviedb.core.ui.utils

/** Converts yyyy-MM-dd → dd.MM.yyyy */
fun formatDate(date: String): String {
    val parts = date.split("-")
    return appDateFormat(parts, date)
}

private fun appDateFormat(
    parts: List<String>,
    date: String
): String = if (parts.size == 3) {
    "${parts[2]}.${parts[1]}.${parts[0]}"
} else {
    date
}

/** Extracts the 4-digit year from a yyyy-MM-dd date string. */
fun formatYear(date: String): String = date.take(4)

/** Converts an ISO 8601 timestamp or date to dd-MM-yyyy. */
fun formatIso8601Date(date: String): String {
    val parts = date.take(10).split("-")
    return appDateFormat(parts, date)
}