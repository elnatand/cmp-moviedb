import androidx.compose.runtime.Composable

interface PlatformViewController

@Composable
expect fun getPlatformViewController(): PlatformViewController
