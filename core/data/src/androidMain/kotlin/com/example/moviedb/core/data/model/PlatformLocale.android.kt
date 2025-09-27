package com.example.moviedb.core.data.model

import java.util.Locale

actual val platformLanguage: String
    get() = Locale.getDefault().language

actual val platformCountry: String
    get() =  Locale.getDefault().country