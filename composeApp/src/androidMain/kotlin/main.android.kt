import androidx.compose.runtime.Composable
import java.util.Locale

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = App()

actual val platformLanguage:String?
    get() = Locale.getDefault().language
