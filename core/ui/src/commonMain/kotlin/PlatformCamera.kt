import androidx.compose.runtime.Composable


expect class CameraFactory(context: PlatformViewController) {
    @Composable
    fun createCamera(): Camera
}

expect class Camera {
    @Composable
    fun RegisterCamera(onImagePicked: (ByteArray) -> Unit)

    fun openCamera()
}