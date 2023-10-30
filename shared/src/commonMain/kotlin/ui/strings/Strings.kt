package ui.strings

import platformLanguage

/**
 * Temporary Strings holder
 */
enum class Strings(
    private val en: String,
    private val iw: String
) {
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
    );

    fun get(): String {
        Strings.valueOf(name).apply {
            return when (platformLanguage) {
                "en" -> en
                "iw" -> iw
                else -> en
            }
        }
    }
}