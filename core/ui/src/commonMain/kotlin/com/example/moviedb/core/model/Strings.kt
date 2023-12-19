package com.example.moviedb.core.model

import platformLanguage


/**
 * Temporary Strings holder
 */
enum class Strings(
    private val en: String,
    private val iw: String
) {
    camera(
        en = "Camera",
        iw = "מצלמה"
    ),
    user_name(
        en = "Name",
        iw = "שם"
    ),
    movies(
        en = "Movies",
        iw = "סרטים"
    ),
    tv_shows(
        en = "TV Shows",
        iw = "סדרות"
    ),
    profile(
        en = "Profile",
        iw = "פרופיל"
    ),
    notifications(
        en = "Notifications",
        iw = "התראות"
    );

    fun get(): String {
        Strings.valueOf(name).apply {
            return when (platformLanguage) {
                "en" -> en
                "iw", "he" -> iw
                else -> en
            }
        }
    }
}