import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIUserInterfaceLayoutDirection
import platform.UIKit.UIViewController

actual val platformLanguage: String?
    get() = NSLocale.currentLocale.languageCode


actual val isRightToLeftLanguage: Boolean
    get() = UIApplication.sharedApplication.userInterfaceLayoutDirection==
            UIUserInterfaceLayoutDirection.UIUserInterfaceLayoutDirectionRightToLeft




actual class Camera(
    private val rootController: UIViewController
) {
    actual fun open() {
        val cameraVc = UIImagePickerController()
        cameraVc.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        rootController.presentViewController(cameraVc, true, completion = null)
    }
}

