import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.memcpy

actual class Camera(
    private val rootController: UIViewController
) {

    private val imagePickerController = UIImagePickerController().apply {
        sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
    }

    private var onImagePicked: (ByteArray) -> Unit = {}

    @OptIn(ExperimentalForeignApi::class)
    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingImage: UIImage,
            editingInfo: Map<Any?, *>?
        ) {
            val imageNsData = UIImageJPEGRepresentation(didFinishPickingImage, 1.0) ?: return
            val bytes = ByteArray(imageNsData.length.toInt())
            memcpy(bytes.refTo(0), imageNsData.bytes, imageNsData.length)

            onImagePicked(bytes)

            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
        }
    }


    actual fun openCamera() {
        rootController.presentViewController(imagePickerController, true) {
            imagePickerController.delegate = delegate
        }
    }

    @Composable
    actual fun RegisterCamera(onImagePicked: (ByteArray) -> Unit) {
        this.onImagePicked = onImagePicked
    }
}


actual class CameraFactory actual constructor(private val context: PlatformViewController) {

    @Composable
    actual fun createCamera(): Camera {
        val rootController = context.iosController.current
        return remember {
            Camera(rootController)
        }
    }
}