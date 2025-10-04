package com.elna.moviedb.core.model

enum class AppTheme(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun getAppThemeByValue(themeValue: String): AppTheme {
            return entries.firstOrNull { it.value == themeValue } ?: SYSTEM
        }
    }
}
