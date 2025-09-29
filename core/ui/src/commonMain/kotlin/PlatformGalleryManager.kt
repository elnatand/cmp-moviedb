import androidx.compose.runtime.Composable

@Composable
expect fun rememberGalleryManager(onResult: (PlatformSharedImage?) -> Unit): GalleryManager


expect class GalleryManager(onLaunch: () -> Unit) {
    fun launch()
}