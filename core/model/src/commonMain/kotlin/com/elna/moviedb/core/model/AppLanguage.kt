package com.elna.moviedb.core.model

enum class AppLanguage(val code: String, val countryCode: String) {
    ENGLISH("en", "US"),
    HINDI("hi", "IN"),
    ARABIC("ar", "SA"),
    HEBREW("he", "IL");


    companion object{
        fun getAppLanguageByCode(languageCode: String): AppLanguage{
            return entries.firstOrNull { it.code == languageCode }?: ENGLISH
        }
    }
}