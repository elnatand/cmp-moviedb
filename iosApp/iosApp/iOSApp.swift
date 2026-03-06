import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        AppModuleKt.iOsInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
