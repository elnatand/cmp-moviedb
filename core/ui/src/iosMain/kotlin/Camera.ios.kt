import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController

actual class Camera(
    private val rootController: UIViewController
) {


    actual fun openCamera() {
        val cameraVc = UIImagePickerController()
        cameraVc.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        rootController.presentViewController(cameraVc, true, completion = null)
    }

    @Composable
    actual fun RegisterCamera(onImagePicked: (ByteArray) -> Unit) {
       // this.onImagePicked = onImagePicked
    }
}


actual class CameraFactory actual constructor(private val context: PlatformContext) {

    @Composable
    actual fun createCamera(): Camera {
        val rootController = context.iosController.current
        return remember {
            Camera(rootController)
        }
    }
}