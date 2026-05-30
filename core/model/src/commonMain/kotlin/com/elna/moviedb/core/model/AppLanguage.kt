package com.elna.moviedb.core.model

enum class AppLanguage(val code: String, val countryCode: String, val isRtl: Boolean = false) {
    ENGLISH("en", "US"),
    HINDI("hi", "IN"),
    ARABIC("ar", "SA", isRtl = true),
    HEBREW("he", "IL", isRtl = true);


    companion object{
        fun getAppLanguageByCode(languageCode: String): AppLanguage{
            return entries.firstOrNull { it.code == languageCode }?: ENGLISH
        }

        /**
         * Whether the given BCP 47 language code renders right-to-left. Falls back to LTR for
         * unknown codes (via [getAppLanguageByCode]). Keeping this on the enum means adding an
         * RTL language only requires setting [isRtl] here — the platform layout-direction logic
         * needs no changes.
         */
        fun isRtl(languageCode: String): Boolean = getAppLanguageByCode(languageCode).isRtl
    }
}