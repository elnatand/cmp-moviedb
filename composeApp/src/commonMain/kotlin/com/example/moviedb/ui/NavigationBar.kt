package com.example.moviedb.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb.navigation.TopLevelDestination


@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: String?,
    onClick: (TopLevelDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        tonalElevation = 0.dp,
        content = {
            topLevelDestinations.forEach {
                val selected = currentDestination == it.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onClick(it) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(it.titleRes.get()) },
                )
            }
        },
    )
}