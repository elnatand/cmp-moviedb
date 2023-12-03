package com.example.moviedb.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import platformLanguage

@Composable
fun LayoutDirectionWrapper(content: @Composable () -> Unit) {
    val layoutDirection = when (platformLanguage) {
        "en" -> LayoutDirection.Ltr
        "iw", "he" -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        content()
    }
}