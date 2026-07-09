import UIKit
import SwiftUI
import ComposeApp

/// Pre-iOS 26 fallback: Compose drives all navigation (tab bar + back stack).
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        if #available(iOS 26.0, *) {
            LiquidGlassContentView()
        } else {
            ComposeView()
                    .ignoresSafeArea(.all) // Compose has own keyboard handler
        }
    }
}

// MARK: - iOS 26+ Liquid Glass shell
//
// SwiftUI owns the TabView and one NavigationStack per tab, so iOS renders native
// Liquid Glass chrome (floating tab bar, glass back button). Compose renders each
// screen's content in its own view controller and forwards navigation events here.
// Routes cross the boundary as opaque JSON tokens (see NativeShell.kt).

@available(iOS 26.0, *)
struct PushedRoute: Hashable, Identifiable {
    let id = UUID()
    let routeJson: String
    let title: String

    static func == (lhs: PushedRoute, rhs: PushedRoute) -> Bool {
        lhs.id == rhs.id
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}

@available(iOS 26.0, *)
@Observable
final class TabNavigationCoordinator {
    var path: [PushedRoute] = []

    func push(routeJson: String, title: String) {
        path.append(PushedRoute(routeJson: routeJson, title: title))
    }

    func pop() {
        if !path.isEmpty {
            path.removeLast()
        }
    }
}

@available(iOS 26.0, *)
@Observable
final class AppNavigationCoordinator {
    struct TabSpec: Identifiable {
        let index: Int32
        let title: String
        let systemImage: String
        let isSearch: Bool
        var id: Int32 { index }
    }

    var selectedTab: Int32 = 0
    /// Resolved app theme reported by Compose (nil until first report → follow system).
    var isDarkTheme: Bool? = nil

    let tabs: [TabSpec]
    private let tabCoordinators: [TabNavigationCoordinator]

    init() {
        // Order mirrors TopLevelDestination in Kotlin (Movies, TV Shows, Search, Profile);
        // titles come from the shared compose resources so they stay localized.
        let symbols: [(image: String, isSearch: Bool)] = [
            ("film", false),
            ("tv", false),
            ("magnifyingglass", true),
            ("person.crop.circle", false),
        ]
        let count = Int(NativeShellKt.tabCount())
        tabs = (0..<count).map { index in
            let symbol = index < symbols.count ? symbols[index] : (image: "circle", isSearch: false)
            return TabSpec(
                index: Int32(index),
                title: NativeShellKt.tabTitle(tabIndex: Int32(index)),
                systemImage: symbol.image,
                isSearch: symbol.isSearch
            )
        }
        tabCoordinators = (0..<count).map { _ in TabNavigationCoordinator() }
    }

    func coordinator(for index: Int32) -> TabNavigationCoordinator {
        tabCoordinators[Int(index)]
    }
}

/// Compose screen that is the root of a native tab.
@available(iOS 26.0, *)
struct TabRootComposeView: UIViewControllerRepresentable {
    let tabIndex: Int32
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        NativeShellKt.TabRootViewController(
            tabIndex: tabIndex,
            onNavigate: { routeJson, title in
                coordinator.push(routeJson: routeJson, title: title)
            },
            onThemeChange: { isDarkTheme in
                appCoordinator.isDarkTheme = isDarkTheme.boolValue
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

/// Compose screen pushed onto a tab's NavigationStack.
@available(iOS 26.0, *)
struct DetailComposeView: UIViewControllerRepresentable {
    let routeJson: String
    let coordinator: TabNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        NativeShellKt.RouteViewController(
            routeJson: routeJson,
            onNavigate: { routeJson, title in
                coordinator.push(routeJson: routeJson, title: title)
            },
            onBack: { coordinator.pop() }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
struct TabContentView: View {
    let tab: AppNavigationCoordinator.TabSpec
    let appCoordinator: AppNavigationCoordinator

    // NavigationStack needs a @State-backed path: binding it straight to the @Observable
    // coordinator lets NavigationStack write back a stale (empty) path from its UIKit
    // willShow callback on iOS 26, popping freshly pushed screens. The coordinator stays
    // the source of truth (Kotlin callbacks mutate it); the two are synced below.
    @State private var path: [PushedRoute] = []

    private var coordinator: TabNavigationCoordinator {
        appCoordinator.coordinator(for: tab.index)
    }

    var body: some View {
        NavigationStack(path: $path) {
            TabRootComposeView(
                tabIndex: tab.index,
                coordinator: coordinator,
                appCoordinator: appCoordinator
            )
            .ignoresSafeArea(.all)
            // Tab roots keep their Compose top bars; only detail screens use the native bar.
            .toolbar(.hidden, for: .navigationBar)
            .navigationDestination(for: PushedRoute.self) { pushed in
                DetailComposeView(
                    routeJson: pushed.routeJson,
                    coordinator: coordinator
                )
                .ignoresSafeArea(.all)
                // Keep the native bar hidden on details too: on iOS 26, taps in the top region
                // over hosted Compose content are unreliable (native toolbar buttons never fire;
                // reproducible on the pure-Compose fallback as well). The Compose screen draws
                // its own back button; back events arrive via Kotlin onBack → coordinator.pop().
                .toolbar(.hidden, for: .navigationBar)
            }
        }
        .onChange(of: coordinator.path) { _, newValue in
            if path != newValue { path = newValue }
        }
        .onChange(of: path) { _, newValue in
            if coordinator.path != newValue { coordinator.path = newValue }
        }
    }
}

@available(iOS 26.0, *)
struct LiquidGlassContentView: View {
    @State private var appCoordinator = AppNavigationCoordinator()

    var body: some View {
        TabView(selection: $appCoordinator.selectedTab) {
            ForEach(appCoordinator.tabs) { tab in
                if tab.isSearch {
                    Tab(tab.title, systemImage: tab.systemImage, value: tab.index, role: .search) {
                        TabContentView(tab: tab, appCoordinator: appCoordinator)
                    }
                } else {
                    Tab(tab.title, systemImage: tab.systemImage, value: tab.index) {
                        TabContentView(tab: tab, appCoordinator: appCoordinator)
                    }
                }
            }
        }
        .tabBarMinimizeBehavior(.automatic)
        .preferredColorScheme(preferredColorScheme)
    }

    private var preferredColorScheme: ColorScheme? {
        guard let isDarkTheme = appCoordinator.isDarkTheme else { return nil }
        return isDarkTheme ? .dark : .light
    }
}
