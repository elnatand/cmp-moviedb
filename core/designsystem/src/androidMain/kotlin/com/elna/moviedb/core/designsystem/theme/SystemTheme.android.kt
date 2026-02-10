package com.elna.moviedb.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme as androidIsSystemInDarkTheme

@Composable
actual fun isSystemInDarkTheme(): Boolean = androidIsSystemInDarkTheme()
