#if canImport(FirebaseCore)
import Foundation
import FirebaseCore
import FirebaseAnalytics

/// Helper class to initialize and configure Firebase for iOS
/// This class should be called from the iOS app's initialization
class FirebaseHelper {

    /// Shared instance
    static let shared = FirebaseHelper()

    private var isConfigured = false

    private init() {}

    /// Configure Firebase if GoogleService-Info.plist exists
    /// Returns true if Firebase was successfully configured
    @discardableResult
    func configure() -> Bool {
        guard !isConfigured else {
            print("✅ Firebase already configured")
            return true
        }

        // Check if GoogleService-Info.plist exists
        guard Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil else {
            print("ℹ️ Firebase Analytics disabled: GoogleService-Info.plist not found")
            return false
        }

        // Configure Firebase
        FirebaseApp.configure()
        isConfigured = true

        // Enable analytics
        Analytics.setAnalyticsCollectionEnabled(true)

        print("✅ Firebase Analytics configured successfully")
        return true
    }

    /// Check if Firebase is configured
    var isAnalyticsEnabled: Bool {
        return isConfigured
    }
}
#endif
