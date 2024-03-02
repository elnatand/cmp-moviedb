import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.interop.LocalUIViewController
import platform.UIKit.UIViewController

class IOSViewController(val iosController: ProvidableCompositionLocal<UIViewController>) : PlatformViewController

@Composable
actual fun getPlatformViewController(): PlatformViewController = IOSViewController(LocalUIViewController)