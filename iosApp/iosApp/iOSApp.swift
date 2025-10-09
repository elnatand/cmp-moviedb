import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        // Initialize Firebase (optional - will only configure if GoogleService-Info.plist exists)
        #if canImport(FirebaseCore)
        FirebaseHelper.shared.configure()
        #else
        print("ℹ️ Firebase not available - skipping initialization")
        #endif

        // Initialize Koin DI
        HelperKt.doInitKoin()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
