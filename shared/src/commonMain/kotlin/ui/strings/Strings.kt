package ui.strings

import platformLanguage

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
        return when (platformLanguage) {
            "en" -> Strings.valueOf(name).en
            "iw" -> Strings.valueOf(name).iw
            else -> Strings.valueOf(name).en
        }
    }
}

