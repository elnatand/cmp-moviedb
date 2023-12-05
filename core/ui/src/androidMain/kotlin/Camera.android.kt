import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

//TODO handle permission
actual class Camera(
    private val activity: ComponentActivity,
) {

    private lateinit var cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
    private lateinit var permissionsActivityLauncher: ActivityResultLauncher<String>
    val REQUEST_IMAGE_CAPTURE = 1

    val context = activity.applicationContext
    val file = context.createImageFile()
    val uri: Uri? = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.example.moviedb" + ".provider", file
    )

    var capturedImageUri: Uri? = null

    @Composable
    actual fun RegisterCamera(onImagePicked: (ByteArray) -> Unit) {
        cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            capturedImageUri = uri
            capturedImageUri?.let {uri->
                context.contentResolver.openInputStream(uri)?.use {
                    onImagePicked(it.readBytes())
                }
            }
        }
    }


    actual fun openCamera() {

        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            // Request a permission
           // permissionLauncher.launch(Manifest.permission.CAMERA)
        }




//        registerActivityResult()
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsActivityLauncher.launch(Manifest.permission.CAMERA)
//        }
    }

    private fun registerActivityResult() {
        permissionsActivityLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: ActivityNotFoundException) {
                    // display error state to the user
                }
            }
    }
}

actual class CameraFactory actual constructor(context: PlatformContext) {

    @Composable
    actual fun createCamera(): Camera {
        val activity = LocalContext.current as ComponentActivity
        return remember(activity) {
            Camera(activity = activity)
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}