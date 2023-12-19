package com.example.moviedb.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import isRightToLeftLanguage


@Composable
fun LayoutDirectionWrapper(content: @Composable () -> Unit) {
    val layoutDirection = when (isRightToLeftLanguage) {
        true -> LayoutDirection.Rtl
        false-> LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        content()
    }
}