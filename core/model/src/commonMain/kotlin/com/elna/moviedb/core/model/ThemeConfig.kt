package com.elna.moviedb.core.model

enum class ThemeConfig(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun getThemeConfigByValue(themeValue: String): ThemeConfig {
            return entries.firstOrNull { it.value == themeValue } ?: SYSTEM
        }
    }
}
