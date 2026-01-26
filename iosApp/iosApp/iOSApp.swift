import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        AppModuleKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
