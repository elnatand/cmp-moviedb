package com.elna.moviedb.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.ui.navigation.Navigator
import com.elna.moviedb.core.ui.navigation.Route

@Composable
fun RootNavGraph(
    rootBackStack: SnapshotStateList<Route>,
    contentPadding: PaddingValues = PaddingValues(),
) {
    // Reserve space for the bottom navigation bar so scrollable content isn't drawn under it.
    //
    // The host Scaffold reports `contentPadding.bottom = bar height + bottom system inset`, but
    // the per-screen Scaffolds below already apply that system inset themselves (and the
    // edge-to-edge detail screens deliberately draw under it). Reserving the full value here
    // would count the system inset twice — which showed up as a gap above the bar on iOS, where
    // the safe-area inset is reported a frame late. So reserve only the *extra* bar height
    // (total minus the live system inset). Both terms track the same inset, so this stays
    // correct frame to frame, and collapses to 0 on detail screens (bar hidden → total == inset).
    //
    // Only the bottom is touched: the top is left to each screen (list screens' TopAppBars inset
    // themselves; detail screens stay full-bleed under the status bar).
    val systemBottomInset = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val bottomBarReservation = (contentPadding.calculateBottomPadding() - systemBottomInset).coerceAtLeast(0.dp)

    val navigator = remember(rootBackStack) { BackStackNavigator(rootBackStack) }

    Box(
        modifier = Modifier.padding(bottom = bottomBarReservation)
    ) {
        SharedTransitionLayout {
            NavDisplay(
                backStack = rootBackStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = appEntryProvider(
                    navigator = navigator,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            )
        }
    }
}

/** [Navigator] for the Compose-driven shell: pushes and pops [backStack]; titles are unused. */
private class BackStackNavigator(
    private val backStack: SnapshotStateList<Route>
) : Navigator {
    override fun navigate(route: Route, title: String) {
        backStack.add(route)
    }

    override fun goBack() {
        backStack.removeLastOrNull()
    }
}
