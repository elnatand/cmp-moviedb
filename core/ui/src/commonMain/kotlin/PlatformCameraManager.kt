import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraManager(onResult: (PlatformSharedImage?) -> Unit): CameraManager


expect class CameraManager(onLaunch: () -> Unit) {
    fun launch()
}