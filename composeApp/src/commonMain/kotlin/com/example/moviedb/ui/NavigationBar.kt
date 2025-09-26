package com.example.moviedb.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviedb.AppState
import com.example.moviedb.navigation.TopLevelDestination
import com.example.moviedb.resources.Res
import com.example.moviedb.resources.movies
import com.example.moviedb.resources.profile
import com.example.moviedb.resources.tv_shows
import org.jetbrains.compose.resources.StringResource
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
                        label = { Text(stringResource(getStringRes(destination))) },
                    )
                }
            },
        )
    }

}

private fun getStringRes(destination: TopLevelDestination): StringResource {
    return when (destination) {
        TopLevelDestination.MOVIES -> Res.string.movies
        TopLevelDestination.TV_SHOWS -> Res.string.tv_shows
        TopLevelDestination.PROFILE -> Res.string.profile
    }
}
