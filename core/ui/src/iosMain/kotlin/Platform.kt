import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual val platformLanguage:String?
    get() = NSLocale.currentLocale.languageCode