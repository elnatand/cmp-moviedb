package com.elna.moviedb.core.ui.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Formats a date string from yyyy-MM-dd to dd.MM.yyyy.
 * Uses kotlinx-datetime for robust parsing.
 */
fun formatDate(date: String): String {
    return try {
        val localDate = LocalDate.parse(date)
        val day = localDate.day.toString().padStart(2, '0')
        val month = localDate.month.number.toString().padStart(2, '0')
        val year = localDate.year
        "$day.$month.$year"
    } catch (_: Exception) {
        date
    }
}
