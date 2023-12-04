import android.util.LayoutDirection
import androidx.core.text.layoutDirection
import java.util.Locale

actual val platformLanguage:String?
    get() = Locale.getDefault().language


actual val isRightToLeftLanguage: Boolean
    get() = Locale.getDefault().layoutDirection == LayoutDirection.RTL
