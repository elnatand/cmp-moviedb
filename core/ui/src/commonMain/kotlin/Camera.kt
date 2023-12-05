import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap


expect class CameraFactory(context: PlatformContext) {
    @Composable
    fun createCamera(): Camera
}

expect class Camera {
    @Composable
    fun RegisterCamera(onImagePicked: (ByteArray) -> Unit)


    fun openCamera()
}