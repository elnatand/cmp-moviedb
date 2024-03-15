import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect class ImagePicker {
    @Composable
    fun RegisterPicker(onImagePicked: (ByteArray) -> Unit)

    fun pickImage()
}

expect class ImagePickerFactory(uiController: PlatformViewController) {
    @Composable
    fun createPicker(): ImagePicker
}

@Composable
expect fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap
