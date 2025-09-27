package com.example.moviedb.core.data.model

import android.util.LayoutDirection
import java.util.Locale

actual val platformLanguage: String
    get() = Locale.getDefault().language
