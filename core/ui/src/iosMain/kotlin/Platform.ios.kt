import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceLayoutDirection

actual val platformLanguage: String?
    get() = NSLocale.currentLocale.languageCode


actual val isRightToLeftLanguage: Boolean
    get() = UIApplication.sharedApplication.userInterfaceLayoutDirection==
            UIUserInterfaceLayoutDirection.UIUserInterfaceLayoutDirectionRightToLeft