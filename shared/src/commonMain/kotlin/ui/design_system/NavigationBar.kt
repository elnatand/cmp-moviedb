package ui.design_system

import TopLevelDestination
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    topLevelDestinations: List<TopLevelDestination>,
    onClick: (TopLevelDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        tonalElevation = 0.dp,
        content = {
            topLevelDestinations.forEach {
                NavigationBarItem(
                    selected = true,
                    onClick = { onClick(it) },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(it.title) },
                )
            }
        },
    )
}