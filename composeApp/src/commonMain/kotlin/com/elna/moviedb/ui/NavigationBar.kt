package com.elna.moviedb.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elna.moviedb.AppState
import org.jetbrains.compose.resources.stringResource


@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    appState: AppState,
) {
    AnimatedVisibility(visible = appState.shouldShowBottomBar()) {
        NavigationBar(
            modifier = modifier,
            tonalElevation = 0.dp,
            content = {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = appState.currentTopLevelDestination == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { appState.navigateToTopLevelDestination(destination) },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(stringResource(destination.titleRes)) },
                    )
                }
            },
        )
    }

}
