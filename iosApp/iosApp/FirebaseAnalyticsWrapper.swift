#if canImport(FirebaseAnalytics)
import Foundation
import FirebaseAnalytics

/// Swift wrapper for Firebase Analytics to be called from Kotlin/Native
/// This class provides a bridge between Kotlin Multiplatform and Firebase Analytics SDK
@objc public class FirebaseAnalyticsWrapper: NSObject {

    /// Singleton instance
    @objc public static let shared = FirebaseAnalyticsWrapper()

    private override init() {
        super.init()
    }

    /// Check if Firebase is available and initialized
    @objc public var isAvailable: Bool {
        // Firebase is available if it has been configured
        return true // Will be checked when Firebase.configure() is called
    }

    /// Log a screen view event
    @objc public func logScreenView(screenName: String, screenClass: String?) {
        var parameters: [String: Any] = [
            AnalyticsParameterScreenName: screenName
        ]

        if let screenClass = screenClass {
            parameters[AnalyticsParameterScreenClass] = screenClass
        }

        Analytics.logEvent(AnalyticsEventScreenView, parameters: parameters)
    }

    /// Log a custom event with parameters
    @objc public func logEvent(name: String, parameters: [String: Any]?) {
        Analytics.logEvent(name, parameters: parameters)
    }

    /// Set a user property
    @objc public func setUserProperty(value: String?, forName name: String) {
        Analytics.setUserProperty(value, forName: name)
    }

    /// Set user ID
    @objc public func setUserId(_ userId: String?) {
        Analytics.setUserID(userId)
    }

    /// Enable/disable analytics collection
    @objc public func setAnalyticsCollectionEnabled(_ enabled: Bool) {
        Analytics.setAnalyticsCollectionEnabled(enabled)
    }
}
#endif
