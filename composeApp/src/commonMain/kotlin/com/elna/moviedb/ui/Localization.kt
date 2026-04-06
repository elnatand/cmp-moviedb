package com.elna.moviedb.ui

import androidx.compose.runtime.Composable

@Composable
expect fun Localization(selectedLanguage: String, content: @Composable () -> Unit)