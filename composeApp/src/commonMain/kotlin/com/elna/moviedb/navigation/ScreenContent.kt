package com.elna.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.Route

/**
 * Renders a single [route] as a standalone screen, outside any [androidx.navigation3.ui.NavDisplay].
 *
 * Used by the iOS 26+ Liquid Glass shell, where SwiftUI's NavigationStack owns the back stack:
 * each screen lives in its own ComposeUIViewController, so navigation is delegated to the host
 * via [onNavigate] (push) and [onBack] (pop) instead of mutating a Compose back stack.
 *
 * The route → screen mapping is [appEntryProvider], the same registry the NavDisplay shell uses,
 * resolved here without a shared-transition scope: screens in separate view controllers can't
 * share one, so the native push/pop animation takes the place of shared elements.
 *
 * [onNavigate] also carries a display title (empty when unknown) for the native toolbar.
 */
@Composable
fun ScreenContent(
    route: Route,
    onNavigate: (route: Route, title: String) -> Unit,
    onBack: () -> Unit,
) {
    val entryProvider = remember(onNavigate, onBack) {
        appEntryProvider(
            navigator = object : Navigator {
                override fun navigate(route: Route, title: String) = onNavigate(route, title)
                override fun goBack() = onBack()
            }
        )
    }
    entryProvider(route).Content()
}
