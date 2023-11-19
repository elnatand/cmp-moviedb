import java.util.Locale

actual val platformLanguage:String?
    get() = Locale.getDefault().language
