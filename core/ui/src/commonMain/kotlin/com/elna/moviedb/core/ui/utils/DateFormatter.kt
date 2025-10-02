package com.elna.moviedb.core.ui.utils

fun formatDate(date: String): String {
    // Expecting date in yyyy-MM-dd format, converting to dd.MM.yyyy
    val parts = date.split("-")
    return if (parts.size == 3) {
        "${parts[2]}.${parts[1]}.${parts[0]}"
    } else {
        date
    }
}