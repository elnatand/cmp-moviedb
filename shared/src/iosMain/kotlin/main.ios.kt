import moe.tlaster.precompose.PreComposeApplication
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = PreComposeApplication { App() }

actual val platformLanguage:String?
    get() = NSLocale.currentLocale.languageCode