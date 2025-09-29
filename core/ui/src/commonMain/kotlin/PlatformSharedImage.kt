

import androidx.compose.ui.graphics.ImageBitmap

expect class PlatformSharedImage {
    fun toByteArray(): ByteArray?
    fun toImageBitmap(): ImageBitmap?
}