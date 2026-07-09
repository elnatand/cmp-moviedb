import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elna.moviedb.core.datastore.settings.AppSettingsPreferences
import com.elna.moviedb.core.model.AppLanguage
import com.elna.moviedb.core.model.AppTheme
import com.elna.moviedb.core.ui.navigation.Route
import com.elna.moviedb.navigation.ScreenContent
import com.elna.moviedb.navigation.TopLevelDestination
import com.elna.moviedb.ui.Localization
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import platform.UIKit.UIViewController

/**
 * Entry points for the iOS 26+ Liquid Glass shell (see iosApp/ContentView.swift).
 *
 * SwiftUI owns the TabView and per-tab NavigationStacks; Compose renders one screen per
 * UIViewController via [ScreenContent]. Routes cross the Kotlin/Swift boundary as JSON
 * (they are already @Serializable for back-stack persistence), so Swift treats them as
 * opaque tokens and no Kotlin navigation types leak into the framework API.
 *
 * iOS versions before 26 keep using [MainViewController], where Compose drives navigation.
 */

private val nativeRouteJson = Json { ignoreUnknownKeys = true }

private fun encodeRoute(route: Route): String =
    nativeRouteJson.encodeToString(Route.serializer(), route)

private fun decodeRoute(routeJson: String): Route =
    nativeRouteJson.decodeFromString(Route.serializer(), routeJson)

/** Number of native tabs; mirrors [TopLevelDestination]. */
@Suppress("unused")
fun tabCount(): Int = TopLevelDestination.entries.size

/**
 * Localized tab title for the native tab bar, resolved from compose resources so all
 * user-facing text stays in Strings.xml. Follows the in-app language because
 * LocalAppLocale persists it to AppleLanguages (applies from the next launch).
 */
@Suppress("unused")
fun tabTitle(tabIndex: Int): String = runBlocking {
    getString(TopLevelDestination.entries[tabIndex].titleRes)
}

/**
 * Root screen of a native tab. Detail navigation is forwarded to Swift via [onNavigate]
 * as (routeJson, title); [onThemeChange] lets SwiftUI mirror the resolved app theme.
 */
@Suppress("unused")
fun TabRootViewController(
    tabIndex: Int,
    onNavigate: (routeJson: String, title: String) -> Unit,
    onThemeChange: (isDarkTheme: Boolean) -> Unit,
): UIViewController = ComposeUIViewController {
    NativeSingleScreen(
        route = TopLevelDestination.entries[tabIndex].route,
        onNavigate = onNavigate,
        onBack = {},
        onThemeChange = onThemeChange,
    )
}

/** A detail screen pushed onto a native NavigationStack. */
@Suppress("unused")
fun RouteViewController(
    routeJson: String,
    onNavigate: (routeJson: String, title: String) -> Unit,
    onBack: () -> Unit,
): UIViewController = ComposeUIViewController {
    NativeSingleScreen(
        route = decodeRoute(routeJson),
        onNavigate = onNavigate,
        onBack = onBack,
    )
}

@Composable
private fun NativeSingleScreen(
    route: Route,
    onNavigate: (routeJson: String, title: String) -> Unit,
    onBack: () -> Unit,
    onThemeChange: ((isDarkTheme: Boolean) -> Unit)? = null,
) {
    val preferencesManager: AppSettingsPreferences = koinInject()

    val selectedLanguage by preferencesManager.getAppLanguageCode()
        .collectAsStateWithLifecycle(AppLanguage.ENGLISH.code)

    val selectedTheme by preferencesManager.getAppTheme()
        .collectAsStateWithLifecycle(AppTheme.SYSTEM.value)

    Localization(selectedLanguage) {
        Theme(selectedTheme, onThemeChange) {
            ScreenContent(
                route = route,
                onNavigate = { newRoute, title -> onNavigate(encodeRoute(newRoute), title) },
                onBack = onBack,
            )
        }
    }
}
