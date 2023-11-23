package com.example.moviedb.core.ui.design_system

import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Loader(
    modifier: Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.width(40.dp),
        color = Color.Blue,
        trackColor = MaterialTheme.colorScheme.secondary,
    )
}