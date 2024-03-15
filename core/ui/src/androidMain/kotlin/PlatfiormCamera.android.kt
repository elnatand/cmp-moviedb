import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri

import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

actual class Camera(
    private val context: Context,
) {

    private lateinit var cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
    private lateinit var permissionsActivityLauncher: ActivityResultLauncher<String>

    private val file = context.createImageFile()
    private var capturedImageUri: Uri? = null
    private val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.example.moviedb.provider",
        file
    )

    @Composable
    actual fun RegisterCamera(onImagePicked: (ByteArray) -> Unit) {
        cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
            capturedImageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use {
                    onImagePicked(it.readBytes())
                }
            }
        }
        registerPermissionResult()
    }

    @Composable
    private fun registerPermissionResult() {
        permissionsActivityLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                when (isGranted) {
                    true -> cameraLauncher.launch(uri)
                    false -> {}
                }
            }
    }

    actual fun openCamera() {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            permissionsActivityLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}


fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}

actual class CameraFactory actual constructor(private val viewController: PlatformViewController) {

    @Composable
    actual fun createCamera(): Camera {
        val context = (viewController as AndroidViewController).androidContext
        return remember(context) {
            Camera(context = context)
        }
    }
}