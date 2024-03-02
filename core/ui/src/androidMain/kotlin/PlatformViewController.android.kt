import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class AndroidViewController(val androidContext: Context): PlatformViewController

@Composable
actual fun getPlatformViewController(): PlatformViewController = AndroidViewController(LocalContext.current)
