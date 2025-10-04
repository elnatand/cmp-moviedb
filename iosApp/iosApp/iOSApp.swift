import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseCrashlytics


@main
struct iOSApp: App {

    init() {
        // Initialize Firebase BEFORE Koin
        FirebaseApp.configure()

        // Enable Crashlytics
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)

        // Initialize Koin
        HelperKt.doInitKoin()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
