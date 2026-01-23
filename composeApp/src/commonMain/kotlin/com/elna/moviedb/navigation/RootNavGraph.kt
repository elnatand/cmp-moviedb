package com.elna.moviedb.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.elna.moviedb.core.navigation.NavigationFactory
import com.elna.moviedb.core.navigation.Route
import org.koin.mp.KoinPlatform.getKoin

/**
 * The root navigation graph for the application.
 *
 * This Composable function sets up the main navigation container. It uses a [NavDisplay] to manage
 * and display the current screen based on the provided `rootBackStack`.
 *
 * It retrieves all registered [NavigationFactory] instances from Koin to build the navigation
 * graph. Each factory is responsible for defining a part of the app's navigation structure.
 *
 * The [SharedTransitionLayout] is used to enable shared element transitions between screens.
 *
 * It also applies essential decorators for state saving (`rememberSaveableStateHolderNavEntryDecorator`)
 * and ViewModel lifecycle management (`rememberViewModelStoreNavEntryDecorator`) to each navigation entry.
 *
 * @param rootBackStack A mutable list representing the navigation back stack. Changes to this list
 *                      will trigger navigation.
 */
@Composable
fun RootNavGraph(
    rootBackStack: SnapshotStateList<Route>,
) {
    val navigationFactories: List<NavigationFactory> = getKoin().getAll()

    SharedTransitionLayout {
        NavDisplay(
            backStack = rootBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                navigationFactories.forEach { factory ->
                    factory.create(this, rootBackStack, this@SharedTransitionLayout)
                }
            }
        )
    }
}
